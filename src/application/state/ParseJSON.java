/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application.state;

import java.util.ArrayList;

/**
 *
 * @author Mateusz
 */
public class ParseJSON {

    private String[] getSimpleAttributes(String document) {
        String clearDocument = document.replace("{", "").replace("}", "");
        String[] tabOfAttributes = clearDocument.split(",");
        return tabOfAttributes;
    }

    public ArrayList<String> getAttributes(String document) {
        String[] tab = getSimpleAttributes(document);
        ArrayList<String> attributes = new ArrayList<>();
        for (String s : tab) {
            String tmp = s.split(":")[0].replace("\"", "");
            if (!tmp.equals("_rev") && !tmp.equals("_id")) {
                if (tmp.equals("klasa")) {
                    attributes.add("class");
                } else {
                    attributes.add(tmp);
                }
            }
        }
        attributes.remove("class");
        attributes.add("class ");
        return attributes;
    }
    
    public ArrayList<String> getValues(String document){
        String[] tab = getSimpleAttributes(document);
        ArrayList<String> list = new ArrayList<>();
        String classa="";
        for(String s:tab){
            String tmp0= s.split(":")[0].replace("\"", "");
            String tmp1= s.split(":")[1].replace("\"", "");
            if(tmp0.equals("_rev")|| tmp0.equals("_id"))
                continue;
            if(tmp0.equals("klasa"))
                classa=tmp1;
            else
                list.add(tmp1);
        }
        
        list.add(classa);
        
        return list;
    }

}
