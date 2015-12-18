/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample;

import application.state.ApplicationState;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Mateusz
 */
public class GUIController implements Initializable {

    @FXML
    TextField URL, port;
    @FXML
    Button saveAddres;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        ApplicationState as = new ApplicationState();
        
        URL.setText(as.sendTabToApplication()[0]);
        port.setText(as.sendTabToApplication()[1]);
    }

    @FXML
    public void onSaveAdresClick(){
        ApplicationState as = new ApplicationState();
        as.fillTabFromApplication(URL.getText(), port.getText());
    }
    
}
