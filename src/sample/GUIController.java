/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample;

import application.state.ApplicationState;
import application.state.ParseJSON;
import couchdb.CouchDBService;
import couchdb.PrepareData;
import dataMining.KMeans;
import dataMining.Szacowanie;
import dataMining.kNN;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jfx.messagebox.MessageBox;
import weka.WekaService;
import weka.core.Instances;

/**
 * Klasa obsługująca GUI programu.
 *
 * @author Mateusz Ślęzak &amp; Marcin Chyła
 */
public class GUIController implements Initializable {

    /**
     * Pole przechowujące funkcjonalność pola tekstowego do wpisywania adresu
     * URL serwera CouchDB.
     */
    @FXML
    TextField URL;

    /**
     * Pole przechowujące funkcjonalność pola tekstowego do wpisywania numeru
     * portu serwera CouchDB.
     */
    @FXML
    TextField port;

    /**
     * Pole przechowujące funkcjonalność pola tekstowego do wyświetlania ścieżki
     * do pliku z danymi, z których ma zostać utworzona nowa baza.
     */
    @FXML
    TextField fileForNewDataBase;

    /**
     * Pole przechowujące funkcjonalność pola tekstowego do wpisywania nazwy
     * nowo tworzonej dazy danych.
     */
    @FXML
    TextField dataBaseName;

    /**
     * Pole przechowujące funkcjonalność pola tekstowego do wpisywania wartości
     * parametru k w algorytmie k-średnich.
     */
    @FXML
    TextField kParam;

    /**
     * Pole przechowujące funkcjonalność pola tekstowego do wyświetlania ścieżki
     * do pliku z danymi, które mają być użyte jako dane testowe w algorytmie
     * KNN.
     */
    @FXML
    TextField fileForKNN;

    /**
     * Pole przechowujące funkcjonalność pola tekstowego do wpisywania nazwy
     * atrybutu decyzyjnego w algorytmie KNN.
     */
    @FXML
    TextField attributeName;

    /**
     * Pole przechowujące funkcjonalność pola tekstowego do wpisywania wartości
     * parametru k w algorytmie KNN.
     */
    @FXML
    TextField kParamKNN;

    /**
     * Pole przechowujące funkcjonalność przycisku do łączenia się z bazą
     * danych.
     */
    @FXML
    Button saveAddres;

    /**
     * Pole przechowujące funkcjonalność przycisku do tworzenia bazy danych.
     */
    @FXML
    Button createDataBase;

    /**
     * Pole przechowujące funkcjonalność przycisku do usuwania bazy danych.
     */
    @FXML
    Button deleteDataBase;

    /**
     * Pole przechowujące funkcjonalność przycisku do tworzenia skupisk w
     * algorytmie k-średnich.
     */
    @FXML
    Button generateMeans;

    /**
     * Pole przechowujące funkcjonalność przycisku do zapisywania wyniku
     * działania algorytmu k-średnich.
     */
    @FXML
    Button saveKMeansResult;

    /**
     * Pole przechowujące funkcjonalność przycisku do otwierania FileChooser'a,
     * którym wskazujemy plik z danymi testowymi w algorytmie KNN.
     */
    @FXML
    Button showFileForKNN;

    /**
     * Pole przechowujące funkcjonalność przycisku do uruchomienia algorytmu
     * KNN.
     */
    @FXML
    Button makeKNN;

    /**
     * Pole przechowujące funkcjonalność przycisku do zapisania wyniku działania
     * algorytmu KNN.
     */
    @FXML
    Button saveKNN;

    /**
     * Pole przechowujące funkcjonalność listy rozwijalnej, wyświetlającej nazwy
     * baz danych (zakładka Przeglądaj).
     */
    @FXML
    ComboBox reviewDataBases;

    /**
     * Pole przechowujące funkcjonalność listy rozwijalnej, wyświetlającej nazwy
     * baz danych (zakładka K-średnich).
     */
    @FXML
    ComboBox kmeans;

    /**
     * Pole przechowujące funkcjonalność listy rozwijalnej, wyświetlającej nazwy
     * baz danych (zakładka KNN - zbiór treningowy).
     */
    @FXML
    ComboBox knnTrain;

    /**
     * Pole przechowujące funkcjonalność listy rozwijalnej, wyświetlającej nazwy
     * baz danych (zakładka KNN - zbiór testowy).
     */
    @FXML
    ComboBox knnTest;

    /**
     * Pole przechowujące funkcjonalność kontrolki wyświetlającej w formie tektu
     * informacje na temat stworzonej bazy.
     */
    @FXML
    Label confirmationImport;

    /**
     * Pole przechowujące funkcjonalność kontrolki wyświetlającej w formie tektu
     * informacje na temat bazy danych (zakładka Przegladaj).
     */
    @FXML
    TextArea showDataBase;

    /**
     * Pole przechowujące funkcjonalność kontrolki wyświetlającej w formie tektu
     * wynik działania algorytmu k-średnich.
     */
    @FXML
    TextArea showKMeansResult;

    /**
     * Pole przechowujące funkcjonalność kontrolki wyświetlającej w formie tektu
     * wynik działania algorytmu KNN.
     */
    @FXML
    TextArea showKNNResult;

    /**
     * Pole przechowujące funkcjonalność kontrolki zawierającej obiekty do
     * łączenia z bazą.
     */
    @FXML
    AnchorPane connectionForm;

    /**
     * Pole przechowujące funkcjonalność kontrolki zawierającej obiekty głównego
     * widoku programu.
     */
    @FXML
    TabPane mainProgramView;

    /**
     * Pole przechowujące funkcjonalność elementu menu służącego do wyświetlenia
     * formularza łączenia się z bazą.
     */
    @FXML
    MenuItem connection;

    /**
     * Pole przechowujące funkcjonalność elementu menu służącego do rozłączania
     * z bazą danych.
     */
    @FXML
    MenuItem disconnection;

    /**
     * Pole przechowujące funkcjonalność obiektu RadioButton, który aktywuje
     * możliwość wskazania bazy z danymi testowymi w algorytmie KNN.
     */
    @FXML
    RadioButton data;

    /**
     * Pole przechowujące funkcjonalność obiektu RadioButton, który aktywuje
     * możliwość pliku z danymi testowymi w algorytmie KNN.
     */
    @FXML
    RadioButton file;

    /**
     * Klasa wykonująca się w momencie startu aplikacji. Wczytuje dane serwera
     * bazy CouchDB oraz ustawia potrzebne listenery.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ApplicationState as = new ApplicationState();
        URL.setText(as.sendTabToApplication()[0]);
        port.setText(as.sendTabToApplication()[1]);

        kParam.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {

                if (generateMeans.isDisable()) {
                    generateMeans.setDisable(false);
                }
            }

        });
        attributeName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
                if (kParamKNN.isDisable()) {
                    kParamKNN.setDisable(false);
                }
            }
        });
        kParamKNN.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
                if (makeKNN.isDisable()) {
                    makeKNN.setDisable(false);
                }
            }
        });

    }

    /**
     * Metoda wykonująca się po naciśnięciu na przycisk 'Połącz' w celu
     * połączenia się z bazą CouchDB.
     */
    @FXML
    public void onSaveAdresClick() {
        ApplicationState as = new ApplicationState();
        as.fillTabFromApplication(URL.getText(), port.getText());
        boolean tmp = true;
        CouchDBService cdbs = new CouchDBService();
        try {
            reviewDataBases.getItems().addAll(cdbs.getDataBasesNames());
            kmeans.getItems().addAll(cdbs.getDataBasesNames());
            knnTrain.getItems().addAll(cdbs.getDataBasesNames());
        } catch (NullPointerException ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Błąd przy łączeniu z serwerem bazy!");
            alert.setContentText("Nie mogłem się połączyć z serwerem bazy "
                    + "danych.\nSprawdź czy podałeś poprawny adres i port,\n"
                    + "a następnie spróbuj ponownie");
            alert.showAndWait();
            tmp = false;
        }
        if (tmp) {
            connectionForm.setVisible(false);

            Main.resize(620, 660);

            mainProgramView.setVisible(true);
            connection.setDisable(true);
            disconnection.setDisable(false);
        }

    }

    /**
     * Metoda, otwierająca FileChooser do wskazania pliku z danymi w celu
     * utworzenia nowej bazy.
     */
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
            confirmationImport.setVisible(false);
        }
    }

    /**
     * Metoda, wykonująca się po wciśnieciu przycisku 'Wczytaj dane i stwórz
     * bazę.'
     */
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
            if (cdbs.createDataBase(dataBaseName.getText().
                    replace(" ", "-")) == false) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setTitle("Błąd przy tworzeniu bazy!");
                alert.setContentText("Baza o podanej nazwie istnieje na serwerze."
                        + "\nZmień nazwę i spróbuj ponownie.");
                alert.showAndWait();
            } else if (dataBaseName.getText().replace(" ", "").equals("")) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setTitle("Błąd przy tworzeniu bazy!");
                alert.setContentText("Nie podano nazwy.\nPodaj nazwę i spróbuj "
                        + "ponownie.");
                alert.showAndWait();

            } else {
                cdbs.importData(fileForNewDataBase.getText(), dataBaseName.
                        getText().toLowerCase().replace(" ", "-"));
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setTitle("Baza utworzona poprawnie!");
                alert.setContentText("Baza została utworzona.\nPlik z danymy "
                        + "został zapisany w bazie.");
                alert.showAndWait();
                WekaService ws = new WekaService(fileForNewDataBase.getText());
                confirmationImport.setText("Zaimportowane dane:\nIlość obietków:"
                        + " " + ws.getInstances().size() + "\nIlość atrybutów: "
                        + ws.getAttributesName().size());
                confirmationImport.setVisible(true);
                reviewDataBases.getItems().removeAll(reviewDataBases.getItems());
                reviewDataBases.getItems().addAll(cdbs.getDataBasesNames());
                kmeans.getItems().removeAll(kmeans.getItems());
                kmeans.getItems().addAll(cdbs.getDataBasesNames());
                knnTrain.getItems().removeAll(cdbs.getDataBasesNames());
                knnTrain.getItems().addAll(cdbs.getDataBasesNames());
            }
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Błąd przy łączeniu z bazą!");
            alert.setContentText("Nie mogłem połączyć się z serwerem bazy "
                    + "danych.\nSprawdź ustawienia i spróbuj ponownie.");
            alert.showAndWait();
        }
        reviewDataBases.setDisable(false);
    }

    /**
     * Metoda uruchamia się po wybraniu bazy z listy rozwijalnej w zakładce
     * Przeglądaj.
     */
    @FXML
    public void onDataBaseCheckBoxSelect() {
        CouchDBService cdbs = new CouchDBService();
        String dataName = "";
        try {
            dataName = reviewDataBases.getValue().toString();
        } catch (NullPointerException ex) {

        }
        if (!dataName.equals("")) {
            ArrayList<String> simple = cdbs.getSimpleDocuments(dataName);
            ArrayList<String> listAttributes = cdbs.getAttributes(simple);
            ArrayList<String> listValues = cdbs.getValues(simple);
            String text = "@RELATION " + dataName + "\n\n";
            for (String s : listAttributes) {
                text = text + s + "\n";
            }
            text = text + "\n@DATA\n";
            int i = 0;
            for (String s : listValues) {
                if (s.equals("")) {
                    continue;
                }
                if (i < listAttributes.size()) {
                    text = text + s + ",";
                    i++;
                } else {
                    text = text + "\n";
                    text = text + s + ",";
                    i = 1;
                }
            }
            showDataBase.setText(text);
            deleteDataBase.setDisable(false);
        }

    }

    /**
     * Metoda uruchamia się po wciśnięciu przycisku 'Usuń wybraną bazę'
     * (zakładka Przeglądaj).
     */
    @FXML
    public void onDeleteDataBaseClick() {
        String name = reviewDataBases.getValue().toString();
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setTitle("Usunąć bazę?");
        alert.setContentText("Czy na pewno chcesz usunąć bazę: " + name + "?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            CouchDBService cdbs = new CouchDBService();
            cdbs.deleteDataBase(name);
            showDataBase.setText("");
            List<String> list = cdbs.getDataBasesNames();
            list.add(name);
            reviewDataBases.getItems().removeAll(list);
            reviewDataBases.getItems().addAll(cdbs.getDataBasesNames());
            kmeans.getItems().removeAll(list);
            kmeans.getItems().addAll(cdbs.getDataBasesNames());
            knnTrain.getItems().removeAll(list);
            knnTrain.getItems().addAll(cdbs.getDataBasesNames());
            deleteDataBase.setDisable(true);
        }

    }

    @FXML
    /**
     * Metoda uruchamia się po wybraniu z menu aplikacji opcji 'Połącz z bazą'.
     */
    public void onConnectionDataBaseClick() {
        connectionForm.setVisible(true);
    }

    /**
     * Metoda uruchamia się po wybraniu z menu aplikacji opcji 'Rozłącz z bazą'.
     */
    @FXML
    public void onDisconnectionDataBaseClick() {
        mainProgramView.setVisible(false);
        disconnection.setDisable(true);
        connection.setDisable(false);
        CouchDBService cdbs = new CouchDBService();
        fileForNewDataBase.setText("");
        dataBaseName.setText("");
        confirmationImport.setText("");
        showDataBase.setText("");
        kParam.setDisable(true);
        kParam.setText("");
        showKMeansResult.setText("");
        saveKMeansResult.setDisable(true);
        generateMeans.setDisable(true);
        kmeans.getItems().removeAll(cdbs.getDataBasesNames());
        reviewDataBases.getItems().removeAll(cdbs.getDataBasesNames());
        knnTrain.getItems().removeAll(cdbs.getDataBasesNames());
        data.setDisable(true);
        file.setDisable(true);
        data.setSelected(false);
        file.setSelected(false);
        knnTest.setDisable(true);
        knnTest.getItems().removeAll(knnTest.getItems());
        connectionForm.setVisible(true);
        showFileForKNN.setDisable(true);
        attributeName.setDisable(true);
        fileForKNN.setText("");
        attributeName.setText("");
        makeKNN.setDisable(true);
        kParamKNN.setText("");
        kParamKNN.setDisable(true);
        showKNNResult.setText("");
        saveKNN.setDisable(true);
        Main.resize(330, 180);
    }

    /**
     * Metoda uruchamia się po wybraniu z menu aplikacji opcji 'O programie'.
     */
    @FXML
    public void onAboutClick() {
        MessageBox.show(null,
                "Program do tworzenia i przeglądania baz danych w technologii"
                + " CouchDB.\nZawiera implementację prostych algorytmów "
                + "eksploracji danych - kNN i k-średnich.\nPrzygotowany "
                + "jako projekt na zaliczenie przedmiotu Specjalistyczne"
                + " Zastosowanie\nInteligentnych Systemów Wspomagania "
                + "Decyzji. \n\n\u00a9 Mateusz Ślęzak & Marcin Chyła 2015",
                "O programie",
                MessageBox.CANCEL);
    }

    /**
     * Metoda uruchamia się po wybraniu bazy z listy rozwijalnej w zakładce
     * K-średnich.
     */
    @FXML
    public void onDataBaseCheckBoxSelectKMeans() {
        String dataName = "";
        try {
            dataName = kmeans.getValue().toString();
        } catch (NullPointerException ex) {

        }
        if (!dataName.equals("")) {
            kParam.setDisable(false);
        }

    }

    /**
     * Metoda uruchamia się po wciśnięciu przycisku 'Oblicz skupienia' (zakładka
     * K-średnich).
     */
    @FXML
    public void onGenerateMeansClick() {
        String dataName = kmeans.getValue().toString();
        CouchDBService cdbs = new CouchDBService();
        ArrayList<String> simple = cdbs.getSimpleDocuments(dataName);
        int valueCount = cdbs.getValues(simple).size();
        int attributesCount = cdbs.getAttributes(simple).size();
        int instancesCount = valueCount / attributesCount;
        int k = 0;
        boolean tmp = false;
        try {
            k = Integer.parseInt(kParam.getText().toString());
        } catch (NumberFormatException ex) {
            tmp = true;
        }
        if (k <= 0 || tmp) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Błędny parametr k!");
            alert.setContentText("Wprowadzono niepoprawny parametr k.\nUpewnij "
                    + "się że wprowadziłeś liczbę całkowitą i spróbuj ponownie.");
            alert.showAndWait();
        } else if (k > (instancesCount / 2.0)) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setHeaderText(null);
            alert.setTitle("Zbyt wysoki parametr k");
            alert.setContentText("Wprowadzono parametr k, który stanowi "
                    + "co najmniej połowę liczby obiektów w bazie.\nGrupowanie "
                    + "dla takiej dużej liczby nie ma praktycznego uzasadnienia."
                    + "\nCzy kontynuować?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                String s = "";
                try {
                    s = generateKMeans(dataName, k);
                } catch (NullPointerException ex) {
                    s = "";
                }
                if (s.equals("")) {
                    alert = new Alert(AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setTitle("Niepoprawny zbiór danych!");
                    alert.setContentText("Dane nie nadają się do grupowania!");
                    alert.showAndWait();
                    showKMeansResult.setText("*****NIEPOPRAWNE DANE!!!*****");
                } else {
                    showKMeansResult.setText(s);
                }
            }
        } else {
            String s = "";
            try {
                s = generateKMeans(dataName, k);
            } catch (NullPointerException ex) {
                s = "";
            }
            if (s.equals("") || s == null) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setTitle("Niepoprawny zbiór danych!");
                alert.setContentText("Dane nie nadają się do grupowania! "
                        + "Zmniejsz parametr k i spróbuj ponownie.");
                alert.showAndWait();

                showKMeansResult.setText("*****NIEPOPRAWNE DANE!!!*****");
            } else {
                showKMeansResult.setText(s);
            }
        }
    }

    /**
     * Metoda uruchamia algorytm k-średnich.
     *
     * @param dataName nazwa bazy danych
     * @param k ilość skupisk
     * @return Wynik działania algorytmu.
     */
    private String generateKMeans(String dataName, int k) {
        KMeans km = new KMeans(dataName, k);
        saveKMeansResult.setDisable(false);
        return km.reviewData();
    }

    /**
     * Metoda uruchamia się po wciśnięciu przycisku 'Zapisz wynik' (zakładka
     * K-średnich).
     */
    @FXML
    public void onSaveKMeansClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz wynik");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.dir"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Pliki tekstowy TXT", "*.txt")
        );
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try {
                PrintWriter zapis = new PrintWriter(file.getPath());
                zapis.println(showKMeansResult.getText());
                zapis.close();
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setTitle("Zapisałem");
                alert.setContentText("Wynik obliczeń został zapisany pod "
                        + "wskazaną ścieżką.");
                alert.showAndWait();
            } catch (FileNotFoundException ex) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Błąd przy zapisie danych!");
                alert.setHeaderText(null);
                alert.setContentText("Nie zapisałem danych.\nSzczegółowe "
                        + "informacje są podane niżej.");
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                String exceptionText = sw.toString();

                Label label = new Label("Stos wyjątku:");

                TextArea textArea = new TextArea(exceptionText);
                textArea.setEditable(false);
                textArea.setWrapText(true);

                textArea.setMaxWidth(Double.MAX_VALUE);
                textArea.setMaxHeight(Double.MAX_VALUE);
                GridPane.setVgrow(textArea, Priority.ALWAYS);
                GridPane.setHgrow(textArea, Priority.ALWAYS);

                GridPane expContent = new GridPane();
                expContent.setMaxWidth(Double.MAX_VALUE);
                expContent.add(label, 0, 0);
                expContent.add(textArea, 0, 1);

                alert.getDialogPane().setExpandableContent(expContent);

                alert.showAndWait();
            }

        }

    }

    /**
     * Metoda uruchamia się po wybraniu bazy z listy rozwijalnej w zakładce
     * K-średnich (zbiór treningowy).
     */
    @FXML
    public void onDataBaseCheckBoxSelectKNN() {
        String dataName = "";
        try {
            dataName = knnTrain.getValue().toString();
        } catch (NullPointerException ex) {

        }
        if (!dataName.equals("")) {
            data.setDisable(false);
            file.setDisable(false);
            knnTest.getItems().removeAll(knnTest.getItems());
            knnTest.setDisable(true);
            data.setSelected(false);
            file.setSelected(false);
            makeKNN.setDisable(true);
            attributeName.setDisable(true);
            attributeName.setText("");
            showFileForKNN.setDisable(true);
            kParamKNN.setText("");
            kParam.setDisable(true);
            makeKNN.setDisable(true);
            saveKNN.setDisable(true);
        }
    }

    /**
     * Metoda uruchamia się po wybraniu opcji 'Baza z danymi' jako źródło danych
     * testowych (zakładka KNN).
     */
    @FXML
    public void onDataSelect() {

        knnTest.setDisable(false);
        CouchDBService cdbs = new CouchDBService();
        List<String> list = cdbs.getDataBasesNames();
        list.remove(knnTrain.getValue());
        knnTest.getItems().addAll(list);

        showFileForKNN.setDisable(true);
        fileForKNN.setText("");
        attributeName.setDisable(true);
        kParamKNN.setDisable(true);
        makeKNN.setDisable(true);
        saveKNN.setDisable(true);
    }

    /**
     * Metoda uruchamia się po wybraniu opcji 'Plik z danymi' jako źródło danych
     * testowych (zakładka KNN).
     */
    @FXML
    public void onFileSelect() {
        attributeName.setDisable(true);
        knnTest.setDisable(true);
        knnTest.getItems().removeAll(knnTest.getItems());
        showFileForKNN.setDisable(false);
        kParamKNN.setDisable(true);
        makeKNN.setDisable(true);
        saveKNN.setDisable(true);

    }

    /**
     * Metoda uruchamia FileChooser do wskazywania pliku z danymi testowymi w
     * algorytmie knn (zakładka KNN).
     */
    @FXML
    public void onShowFileForKNNClick() {
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
            fileForKNN.setText(file.getAbsoluteFile().toString());
            attributeName.setDisable(false);
        }
    }

    /**
     * Metoda uruchamia się po wybraniu bazy z listy rozwijalnej w zakładce
     * K-średnich (zbiór testowy).
     */
    @FXML
    public void onKNNTestSelect() {
        String s = "";
        try {
            s = knnTest.getValue().toString();
        } catch (NullPointerException ex) {

        }
        if (!s.equals("")) {
            attributeName.setDisable(false);
        }
    }

    /**
     * Metoda uruchamia się po wciśnięciu przycisku 'Klasyfikuj' (zakładka KNN).
     */
    @FXML
    public void onMakeKNNClick() {
        String attName = attributeName.getText();
        if (attName.equals("class")) {
            attName = "class ";
        }
        boolean tmp = true;
        int k = 0;
        try {
            k = Integer.parseInt(kParamKNN.getText());
        } catch (NumberFormatException ex) {
            tmp = false;
        }
        if (!tmp) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Błędny parametr k!");
            alert.setContentText("Wprowadzono niepoprawny parametr k.\nUpewnij "
                    + "się że wprowadziłeś liczbę całkowitą i spróbuj ponownie.");
            alert.showAndWait();
            showKNNResult.setText("");
        } else {
            String dataNameTraining = knnTrain.getValue().toString();
            CouchDBService cdbs = new CouchDBService();
            ParseJSON p = new ParseJSON();
            ArrayList<String> simpleTraining = cdbs.getSimpleDocuments(dataNameTraining);
            ArrayList<String> listOfAttributesTraining = p.getAttributes(simpleTraining.get(0));
            ArrayList<String> listOfValueasTraining = cdbs.getValues(simpleTraining);
            ArrayList<String> listOfValues = new ArrayList<>();
            for (String s : listOfValueasTraining) {
                if (!s.equals("")) {
                    listOfValues.add(s);
                }
            }

            if (listOfValues.size() / listOfAttributesTraining.size() < k) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setTitle("Błędny parametr k!");
                alert.setContentText("Wprowadzono zbyt duży parametr k."
                        + " Wprowadzona liczba jest większa niż liczba "
                        + "elementów zbioru treningowego. Zmień wartość i "
                        + "spróbuj ponownie.");
                alert.showAndWait();
                showKNNResult.setText("");
            } else {
                tmp = false;
                for (String s : listOfAttributesTraining) {
                    if (s.equals(attName)) {
                        tmp = true;
                        break;
                    }
                }
                if (!tmp) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setTitle("Zły atrybut decyzyjny!");
                    alert.setContentText("Atrybut decyzyjny o podanej nazwie nie "
                            + "występuje w zbiorze danych treningowych.\nZmień "
                            + "atrybut i spróbuj ponownie");
                    alert.showAndWait();
                    showKNNResult.setText("");
                }
                if (data.isSelected() && tmp) {
                    String dataNameTest = knnTest.getValue().toString();
                    ArrayList<String> simpleTest = cdbs.getSimpleDocuments(dataNameTest);
                    ArrayList<String> listOfAttributesTest = p.getAttributes(simpleTest.get(0));
                    tmp = false;
                    for (String s : listOfAttributesTest) {
                        if (attName.equals(s)) {
                            tmp = true;
                            break;
                        }
                    }
                    if (!tmp) {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setHeaderText(null);
                        alert.setTitle("Zły atrybut decyzyjny!");
                        alert.setContentText("Atrybut decyzyjny o podanej nazwie nie "
                                + "występuje w zbiorze danych testowych.\nZmień "
                                + "atrybut i spróbuj ponownie");
                        alert.showAndWait();
                        showKNNResult.setText("");
                    }
                    if (listOfAttributesTest.size() != listOfAttributesTraining.size() && tmp) {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setHeaderText(null);
                        alert.setTitle("Różna ilość atrybutów!");
                        alert.setContentText("Zbiory danych testowych i treningowych "
                                + "nie zawierają danych tego samego typu.");
                        alert.showAndWait();
                        showKNNResult.setText("");
                        tmp = false;
                    }

                    if (tmp) {
                        for (int i = 0; i < listOfAttributesTest.size(); i++) {
                            if (!listOfAttributesTest.get(i).
                                    equals(listOfAttributesTraining.get(i))) {
                                tmp = false;
                                break;
                            }
                        }
                        if (!tmp) {
                            Alert alert = new Alert(AlertType.ERROR);
                            alert.setHeaderText(null);
                            alert.setTitle("Różne atrybuty!");
                            alert.setContentText("Zbiory danych testowych i treningowych "
                                    + "nie zawierają danych tego samego typu.");
                            alert.showAndWait();
                            showKNNResult.setText("");
                        } else {
                            PrepareData testPrepare = new PrepareData(knnTest.getValue().toString());
                            PrepareData trainingPrepare = new PrepareData(knnTrain.getValue().toString());

                            showKNNResult.setText(kNNFromCouch(k, trainingPrepare.getDataForWeka(), testPrepare.getDataForWeka(), attName));
                        }
                    }
                } else if (file.isSelected() && tmp) {
                    if (attName.equals("class")) {
                        attName = "klasa";
                    }
                    tmp = false;
                    WekaService ws = new WekaService(fileForKNN.getText());
                    ArrayList<String> listOfAttributesTest = ws.getAttributesName();
                    for (String s : listOfAttributesTest) {
                        if (s.equals(attName)) {
                            tmp = true;
                            break;
                        }
                    }
                    if (!tmp) {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setHeaderText(null);
                        alert.setTitle("Zły atrybut decyzyjny!");
                        alert.setContentText("Atrybut decyzyjny o podanej nazwie nie "
                                + "występuje w zbiorze danych testowych.\nZmień "
                                + "atrybut i spróbuj ponownie");
                        alert.showAndWait();
                        showKNNResult.setText("");
                    }
                    if (listOfAttributesTest.size() != listOfAttributesTraining.size() && tmp) {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setHeaderText(null);
                        alert.setTitle("Różna ilość atrybutów!");
                        alert.setContentText("Zbiory danych testowych i treningowych "
                                + "nie zawierają danyych tego samego typu.");
                        alert.showAndWait();
                        showKNNResult.setText("");
                        tmp = false;
                    }

                    if (tmp) {
                        for (int i = 0; i < listOfAttributesTest.size(); i++) {
                            String s = listOfAttributesTest.get(i);
                            if (!listOfAttributesTraining.contains(s)) {
                                tmp = false;
                                break;
                            }
                        }
                        if (!tmp) {
                            Alert alert = new Alert(AlertType.ERROR);
                            alert.setHeaderText(null);
                            alert.setTitle("Różne atrybuty!");
                            alert.setContentText("Zbiory danych testowych i treningowych "
                                    + "nie zawierają danyych tego samego typu.");
                            alert.showAndWait();
                            showKNNResult.setText("");
                        } else {
                            PrepareData prepare = new PrepareData(knnTrain.getValue().toString());
                            showKNNResult.setText(kNNFromFile(k, prepare.getDataForWeka(), fileForKNN.getText(), attName));
                        }
                    }

                }
            }
        }
    }

    /**
     * Metoda uruchamia algorytm KNN dla danych pochodzących wyłącznie z bazy
     * CouchDB.
     *
     * @param k wartość parametru k
     * @param training zbiór danych treningowych typu Instances
     * @param test zbiór danych testowych typu Instances
     * @param name nazwa atrybutu decyzyjnego
     * @return Wynik działania algorytmu.
     */
    private String kNNFromCouch(int k, Instances training, Instances test, String name) {
        kNN make = new kNN(k, training, test, name);
        String klasyfikacja = make.reviewData();
        Szacowanie s = new Szacowanie(make.getData(), test);
        String str = "@RELATION " + knnTest.getValue().toString() + "KNN\n\n";
        CouchDBService cdbs = new CouchDBService();
        ArrayList<String> simple = cdbs.getSimpleDocuments(knnTest.getValue().toString());
        ArrayList<String> listAttributes = cdbs.getAttributes(simple);
        for (String s1 : listAttributes) {
            str = str + s1 + "\n";
        }
        str = str + "\n@DATA\n";
        String szacowanie = s.reviewData();
        saveKNN.setDisable(false);
        return szacowanie + "\n" + str + klasyfikacja;
    }

    /**
     * Metoda uruchamia algorytm KNN dla danych testowych pochodzących z pliku.
     *
     * @param k wartość parametru k
     * @param training zbiór danych treningowych typu Instances
     * @param test ścieżka do pliku z danymi testowymi
     * @param name nazwa atrybutu decyzyjnego
     * @return Wynik działania algorytmu.
     */
    private String kNNFromFile(int k, Instances training, String test, String name) {
        WekaService ws = new WekaService(test);
        kNN make = new kNN(k, training, ws.getData(), name);
        String klasyfikacja = make.reviewData();
        Szacowanie s = new Szacowanie(make.getData(), ws.getData());
        String[] tmp = fileForKNN.getText().split("\\");
        String complexName = tmp[tmp.length - 1];
        String relationName = complexName.substring(0, complexName.length() - 1);
        String str = "@RELATION " + relationName + "KNN\n\n";
        Instances ins = ws.getData();
        for (int i = 0; i < ws.getData().numAttributes(); i++) {
            str = str + ins.attribute(i).toString() + "\n";
        }
        str = str + "\n@DATA\n";
        String szacowanie = s.reviewData();
        saveKNN.setDisable(false);
        return szacowanie + "\n" + str + klasyfikacja;
    }

    /**
     * Metoda uruchamia się po wciśnięciu przycisku 'Zapisz wynik' (zakładka
     * KNN).
     */
    @FXML
    public void onSaveKNNClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz wynik");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.dir"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Pliki ARFF", "*.arff")
        );
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try {
                PrintWriter zapis = new PrintWriter(file.getPath());
                zapis.println(showKNNResult.getText());
                zapis.close();
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setTitle("Zapisałem");
                alert.setContentText("Wynik obliczeń został zapisany pod "
                        + "wskazaną ścieżką.");
                alert.showAndWait();
            } catch (FileNotFoundException ex) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Błąd przy zapisie danych!");
                alert.setHeaderText(null);
                alert.setContentText("Nie zapisałem danych.\nSzczegółowe "
                        + "informacje są podane niżej.");
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                String exceptionText = sw.toString();

                Label label = new Label("Stos wyjątku:");

                TextArea textArea = new TextArea(exceptionText);
                textArea.setEditable(false);
                textArea.setWrapText(true);

                textArea.setMaxWidth(Double.MAX_VALUE);
                textArea.setMaxHeight(Double.MAX_VALUE);
                GridPane.setVgrow(textArea, Priority.ALWAYS);
                GridPane.setHgrow(textArea, Priority.ALWAYS);

                GridPane expContent = new GridPane();
                expContent.setMaxWidth(Double.MAX_VALUE);
                expContent.add(label, 0, 0);
                expContent.add(textArea, 0, 1);

                alert.getDialogPane().setExpandableContent(expContent);

                alert.showAndWait();
            }
        }
    }
}
