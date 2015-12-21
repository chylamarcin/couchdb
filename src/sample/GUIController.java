/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample;

import application.state.ApplicationState;
import couchdb.CouchDBService;
import java.io.File;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Mateusz
 */
public class GUIController implements Initializable {

    @FXML
    TextField URL, port, fileForNewDataBase, dataBaseName;
    @FXML
    Button saveAddres, createDataBase;

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
    public void onSaveAdresClick() {
        ApplicationState as = new ApplicationState();
        as.fillTabFromApplication(URL.getText(), port.getText());
    }

    @FXML
    public void onCreateDataBaseFileChooseClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wskaż plik z danymi...");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.dir"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Pliki w formacie ARFF", "*.arff")
        );
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            fileForNewDataBase.setText(file.getAbsoluteFile().toString());
            String name = file.getName().substring(0, file.getName().length() - 5);
            dataBaseName.setText(name);
            dataBaseName.setDisable(false);
            createDataBase.setDisable(false);
        }
    }

    @FXML
    public void onCreateDataBaseClick() {
        CouchDBService cdbs = new CouchDBService();
        boolean tmp = true;
        try {
            cdbs.checkServer();
        } catch (UnknownHostException ex) {
            tmp = false;
        }
        if (tmp) {
            if (cdbs.createDataBase(dataBaseName.getText()) == false) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setTitle("Błąd przy tworzeniu bazy!");
                alert.setContentText("Baza o podanej nazwie istnieje na serwerze.\nZmień nazwę i spróbuj ponownie.");
                alert.showAndWait();
            } else {
                cdbs.importData(fileForNewDataBase.getText(), dataBaseName.getText());
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setTitle("Baza utworzona poprawnie!");
                alert.setContentText("Baza została utworzona.\nPlik z danymy został zapisany w bazie.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setTitle("Błąd przy łączeniu z bazą!");
                alert.setContentText("Nie mogłem połączyć się z serwerem bazy "
                        + "danych.\nSprawdź ustawienia i spróbuj ponownie.");
                alert.showAndWait();
        }

    }

}
