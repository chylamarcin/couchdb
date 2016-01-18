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
import java.util.HashMap;
import java.util.List;
import weka.WekaService;
import weka.core.Instance;

/**
 * Klasa do obsługi bazy CouchDB.
 *
 * @author Mateusz Ślęzak &amp; Marcin Chyła
 */
public class CouchDBService {

    /**
     * Pole, które przechowuje adres URL serwera bazy CouchDB.
     */
    private String URL;

    /**
     * Pole, które przechowuje numer portu serwera bazy CouchDB.
     */
    private int port;

    /**
     * Pole, które przechowuje sesję aplikacji z serwerem bazy CouchDB.
     */
    private Session session;

    /**
     * Konstruktor klasy. Pobiera dane do połączenia się z bazą CouchDB z bazy
     * SQLite i zapisuje je do odpowiednich pól klasy. Tworzy nową sesję serwera
     * CouchDB.
     */
    public CouchDBService() {
        ApplicationState as = new ApplicationState();
        String[] tab = as.sendTabToApplication();
        URL = tab[0];
        port = Integer.parseInt(tab[1]);
        session = new Session(URL, port);
    }

    /**
     * Metoda, która sprawdza czy serwer CouchDB jest dostępny.
     *
     * @throws UnknownHostException Wyjątek jest wyrzucany w przypadku, gdy
     * serwer jest niedostępny.
     */
    public void checkServer() throws UnknownHostException {
        try {
            List<String> list = session.getDatabaseNames();
        } catch (NullPointerException ex) {
            throw new UnknownHostException();
        }
    }

    /**
     * Metoda, która tworzy nową bazę na serwerze CouchDB.
     *
     * @param name nazwa bazy
     * @return True, jeśli baza została utworzona, false w przypadku gdy baza o
     * podanej nazwie już istnieje.
     */
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

    /**
     * Metoda do zapisywania danych do bazy.
     *
     * @param fileName nazwa pliku z danymi, akceptowalny format to pliki ARFF
     * @param dataBaseName nazwa bazy danych
     */
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

    /**
     * Metoda, która tworzy listę nazw baz danych na serwerze.
     *
     * @return List zawierająca nazwy wszystkich baz z serwera.
     */
    public List<String> getDataBasesNames() {
        List<String> list = session.getDatabaseNames();
        list.remove("_replicator");
        list.remove("_users");
        return list;
    }

    /**
     * Metoda do pobierania dokumentów JSON z bazy.
     *
     * @param name nazwa bazy
     * @return ArrayList zawierająca wszystkie dokumenty w bazie. Dokumenty
     * przechowywane w liście jako typ String.
     */
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

    /**
     * Metoda, do pobierania wartości z bazy.
     *
     * @param listOfDocuments lista dokumentów z bazy skonwertowanych do typu
     * String
     * @return ArrayList przechowywująca wartości pobrane z dokumentów podanych
     * jako parametr. Wartości przechowywane są jako typ String.
     */
    public ArrayList<String> getValues(ArrayList<String> listOfDocuments) {
        ParseJSON p = new ParseJSON();
        ArrayList<String> listOfValues = new ArrayList<>();
        for (String s : listOfDocuments) {
            listOfValues.addAll(p.getValues(s));
        }
        return listOfValues;
    }

    /**
     * Metoda, do pobierania atrybutów z listy dokumentów JSON skonwertowanych
     * do typu String podanej jako parametr.
     *
     * @param simpleDocuments lista dokumentów
     * @return ArrayList zawierająca nazwy atrybutów, występujących w
     * dokumentach podanych jako parametr.
     */
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
                ArrayList<String> list = new ArrayList<>();
                list = isNominal(i, listOfValues, simpleDocuments);
                if (list == null) {
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

    /**
     * Metoda do sprawdzania czy atrybut, którego pozycja została podana jako
     * parametr, przechowuje wartości nominalne.
     *
     * @param positionOfAttribute pozycja (numer) sprawdzanego atrybutu
     * @param listOfValues lista wartości
     * @param simpleDocuments lista dokumentów JSON skonwertowanych do typu
     * String
     * @return Null jeśli atrybyt nie posiada wartości ze skali nominalnej, w
     * przeciwnym wypadku ArrayList z możliwymi wartościami atrybutu.
     */
    public ArrayList<String> isNominal(int positionOfAttribute,
            ArrayList<String> listOfValues, ArrayList<String> simpleDocuments) {
        ArrayList<String> listOfNominal = new ArrayList<>();
        ParseJSON p = new ParseJSON();
        String attribute = p.getAttributes(simpleDocuments.get(0))
                .get(positionOfAttribute);
        ArrayList<HashMap<String, String>> simple = new ArrayList<>();
        for (String s : simpleDocuments) {
            simple.add(p.getSimpleAttributesHashMap(s));
        }
        for (HashMap<String, String> s : simple) {
            for (String ss : s.keySet()) {
                if (ss.equals(attribute) && !listOfNominal.contains(s.get(ss))) {
                    listOfNominal.add(s.get(ss));
                }
            }
        }
        float countNominal = listOfNominal.size();
        float countValues = listOfValues.size();
        float div = countNominal / countValues;

        if (div < 0.1) {
            return listOfNominal;
        } else {
            return null;
        }
    }

    /**
     * Metoda do usuwania bazy.
     *
     * @param name nazwa bazy do usunięcia
     * @return True jeśli baza została usunięta, false w przeciwnym wypadku.
     */
    public boolean deleteDataBase(String name) {
        return session.deleteDatabase(name);
    }

}
