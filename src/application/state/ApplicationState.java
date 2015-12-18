/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application.state;

/**
 *
 * @author Mateusz
 */
public class ApplicationState {
    
    private String URL;
    private String port;
    private String [] tab;
    
    public ApplicationState(){
        fillTabFromSettings();
        URL=tab[0];
        port=tab[1];
    }
    
    private void fillTabFromSettings(){
        ApplicationSettings as = new ApplicationSettings();
        tab =as.getAddressDB();
    }
    
    public void fillTabFromApplication(String URL, String port){
        tab[0]=URL;
        tab[1]=port;
        saveTab();
    }    
    
    public String[] sendTabToApplication(){
        return tab;
    }
    
    private boolean saveTab(){
        ApplicationSettings as = new ApplicationSettings();
        return as.setAdressDB(tab);
    }
}
