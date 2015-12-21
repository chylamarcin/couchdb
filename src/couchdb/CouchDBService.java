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
    
    public List<String> getDataBasesNames(){
        List<String> list=session.getDatabaseNames();
        list.remove("_replicator");
        list.remove("_users");
        return list;
    }
    
    public String getSimpleDocument(String name){
        Database db = session.getDatabase(name);
        ViewResults result = db.getAllDocuments();
        List<Document> list = result.getResults();
        String id = list.get(0).getJSONObject().getString("id");
        Document doc = db.getDocument(id);
        String document=doc.toString();
                
        return document;
    }

}
