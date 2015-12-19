/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package couchdb;

import application.state.ApplicationState;
import com.fourspaces.couchdb.Session;
import java.util.List;

/**
 *
 * @author Mateusz
 */
public class CouchDBService {
    
    private String URL;
    private int port;
    public CouchDBService(){
        ApplicationState as = new ApplicationState();
        String [] tab = as.sendTabToApplication();
        URL=tab[0];
        port = Integer.parseInt(tab[1]);
    }
    
    public boolean createDataBase(String name){
        Session session = new Session("localhost", 5984);
        List<String> list =session.getDatabaseNames();
        for(String s:list){
            if(s.equals(name))
                return false;
        }
        session.createDatabase(name);
        return true;
    }

    
    
}
