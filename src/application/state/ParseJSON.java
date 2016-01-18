/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application.state;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Klasa do parsowania dokumentów w formacie JSON.
 *
 * @author Mateusz Ślęzak &amp; Marcin Chyła
 */
public class ParseJSON {

    /**
     * Metoda, która z dokumentów JSON usuwa otwierającą i zamykającą tabelę
     * oraz dzieli pozostałe dane. Znakiem podziału jest przecinek.
     *
     * @param document dokument, który ma zostać sparsowany
     * @return Tablica z pojedynczymi zestawami danych typu: atrybut:wartość.
     */
    private String[] getSimpleAttributes(String document) {
        String clearDocument = document.replace("{", "").replace("}", "");
        String[] tabOfAttributes = clearDocument.split(",");
        return tabOfAttributes;
    }

    /**
     * Metoda, która z dokumentu JSON tworzy HashMap'ę, gdzie kluczem jest nazwa
     * atrybutu, wartością w HashMap jest wartość atrubutu z dokumentu JSON.
     *
     * @param document dokument JSON, zapisany w typie String
     * @return HashMap zawierająca wartości &lt;String, String&gt;, gdzie kluczem jest
     * nazwa atrubutu, wartością, wartość jaką atrubut przyjął.
     */
    public HashMap<String, String> getSimpleAttributesHashMap(String document) {
        String[] tabOfAttributes = getSimpleAttributes(document);
        HashMap<String, String> simple = new HashMap<>();
        for (String s : tabOfAttributes) {
            String[] tmp = s.replace("\"", "").split(":");
            String name = tmp[0];
            String value = tmp[1];
            if (name.equals("klasa")) {
                name = "class ";
            }
            if (value.equals("?")) {
                value = "nn";
            }
            simple.put(name, value);
        }
        return simple;
    }

    /**
     * Metoda, która z dokumentu JSON tworzy ArrayList nazw atrybutów, jakie w
     * nim występują.
     *
     * @param document dokument JSON, zapisany w typie String
     * @return ArrayList zawierająca nazwy atrybutów.
     */
    public ArrayList<String> getAttributes(String document) {
        boolean tmpb = false;
        String[] tab = getSimpleAttributes(document);
        ArrayList<String> attributes = new ArrayList<>();
        for (String s : tab) {
            String tmp = s.split(":")[0].replace("\"", "");
            if (!tmp.equals("_rev") && !tmp.equals("_id")) {
                if (tmp.equals("klasa")) {
                    tmpb = true;
                    attributes.add("class");
                } else {
                    attributes.add(tmp);
                }
            }
        }
        if (tmpb) {
            attributes.remove("class");
            attributes.add("class ");
        }
        return attributes;
    }

    /**
     * Metoda, która z dokumentu JSON tworzy ArrayList wartości, jakie dokument
     * zawiarał.
     *
     * @param document dokument JSON, zapisany w typie String
     * @return ArrayList wypełniona wartościami z dokumentu JSON.
     */
    public ArrayList<String> getValues(String document) {
        String[] tab = getSimpleAttributes(document);
        ArrayList<String> list = new ArrayList<>();
        String classa = "";
        for (String s : tab) {
            String tmp0 = s.split(":")[0].replace("\"", "");
            String tmp1 = s.split(":")[1].replace("\"", "");
            if (tmp0.equals("_rev") || tmp0.equals("_id")) {
                continue;
            }
            if (tmp0.equals("klasa")) {
                if (tmp1.equals("?")) {
                    classa = "nn";
                } else {
                    classa = tmp1;
                }
            } else if (tmp1.equals("?")) {
                list.add("nn");

            } else {
                list.add(tmp1);
            }
        }
        list.add(classa);
        return list;
    }

}
