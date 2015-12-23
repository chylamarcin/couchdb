/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package couchdb;

import application.state.ApplicationState;
import application.state.ParseJSON;
import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Document;
import com.fourspaces.couchdb.Session;
import com.fourspaces.couchdb.ViewResults;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import weka.WekaService;
import weka.core.Instance;

/**
 *
 * @author Mateusz
 */
public class CouchDBService {

    private String URL;
    private int port;
    private Session session;

    public CouchDBService() {
        ApplicationState as = new ApplicationState();
        String[] tab = as.sendTabToApplication();
        URL = tab[0];
        port = Integer.parseInt(tab[1]);
        session = new Session(URL, port);
    }

    public void checkServer() throws UnknownHostException {
        try {
            List<String> list = session.getDatabaseNames();
        } catch (NullPointerException ex) {
            throw new UnknownHostException();
        }
    }

    public boolean createDataBase(String name) {
        List<String> list = session.getDatabaseNames();
        for (String s : list) {
            if (s.equals(name)) {
                return false;
            }
        }
        session.createDatabase(name);
        return true;
    }

    public void importData(String fileName, String dataBaseName) {
        WekaService ws = new WekaService(fileName);
        ArrayList<String> listOfAttributesNames = ws.getAttributesName();
        ArrayList<Instance> listOfInstances = ws.getInstances();
        Database db = session.getDatabase(dataBaseName);
        for (int i = 0; i < listOfInstances.size(); i++) {
            Instance instance = listOfInstances.get(i);
            Document doc = new Document();
            doc.setId(String.valueOf(i));
            for (int j = 0; j < instance.numAttributes(); j++) {
                doc.put(listOfAttributesNames.get(j), instance.toString(j));
            }
            db.saveDocument(doc);
        }

    }

    public List<String> getDataBasesNames() {
        List<String> list = session.getDatabaseNames();
        list.remove("_replicator");
        list.remove("_users");
        return list;
    }

    public ArrayList<String> getSimpleDocuments(String name) {
        Database db = session.getDatabase(name);
        ViewResults result = db.getAllDocuments();
        List<Document> list = result.getResults();
        ArrayList<String> listOfDocuments = new ArrayList<>();
        for (Document d : list) {
            String id = d.getJSONObject().getString("id");
            Document doc = db.getDocument(id);
            listOfDocuments.add(doc.toString());
        }

        return listOfDocuments;
    }

    public ArrayList<String> getValues(ArrayList<String> listOfDocuments) {
        
        ParseJSON p = new ParseJSON();
        ArrayList<String> listOfValues = new ArrayList<>();
        for (String s : listOfDocuments) {

            listOfValues.addAll(p.getValues(s));

        }
        return listOfValues;
    }

    public ArrayList<String> getAttributes(ArrayList<String> simpleDocuments) {
        ArrayList<String> listOfValues = getValues(simpleDocuments);
        ParseJSON p = new ParseJSON();
        ArrayList<String> listOfSimpleAttributes
                = p.getAttributes(simpleDocuments.get(0));
        ArrayList<String> listOfComplexAttributes = new ArrayList<>();
        int i = 0;
        String type = "";
        
        for (String s : listOfSimpleAttributes) {
            String attribut = "@ATTRIBUTE\t" + s + "\t";
            try {
                float tmp = Float.parseFloat(listOfValues.get(i));
                type = "REAL";
            } catch (NumberFormatException ex) {

                ArrayList<String> list = isNominal(i,listOfValues,listOfSimpleAttributes);
                if (list.isEmpty()) {
                    type = "STRING";
                } else {
                    type = "\t{";
                    for (String ss : list) {
                        if (list.indexOf(ss) == list.size() - 1) {
                            type = type + ss + "}";
                        } else {
                            type = type + ss + ",";
                        }
                    }
                }
            }
            attribut = attribut + type;
            listOfComplexAttributes.add(attribut);
            i++;
        }
        return listOfComplexAttributes;
    }

    private ArrayList<String> isNominal(int positionOfAttribute,
            ArrayList<String> listOfValues, ArrayList<String> listOfSimpleAttributes) {

        ArrayList<String> listOfNominal = new ArrayList<>();
        int i = positionOfAttribute;
        int numberOfAttributes = listOfSimpleAttributes.size();
        while (i < listOfValues.size()) {
            
            String tmp = listOfValues.get(i);
            if (!listOfNominal.contains(tmp)) {
                listOfNominal.add(tmp);
            }
            i = i + numberOfAttributes;
        }
        float countNominal = listOfNominal.size();
        float countValues = listOfValues.size() / numberOfAttributes;
        float tmp = countNominal / countValues;
        if (tmp < 0.5) {
            return listOfNominal;
        } else {
            return null;
        }
    }

}
