package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Główna klasa Aplikacji.
 *
 * @author Mateusz Ślęzak &amp; Marcin Chyła
 */
public class Main extends Application {

    /**
     * Pole przechowujące dane wyświetlanego okna.
     */
    private static Stage stage2;

    /**
     * Metoda ustawiająca początkowe wartości okna
     *
     * @param primaryStage parametr typu Stage
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("GUI.fxml"));
        stage2 = primaryStage;
        stage2.setTitle("CouchDB Data Mining");

        stage2.setScene(new Scene(root));
        stage2.show();
    }

    /**
     * Główna metoda aplikacji.
     * 
     * @param args argumenty metody main
     */
    public static void main(String[] args) {

        launch(args);
    }

    /**
     * Metoda do zmiany rozmiarów okna aplikacji.
     *
     * @param x nowa długość
     * @param y nowa wysokość
     */
    public static void resize(double x, double y) {

        stage2.setHeight(y);
        stage2.setWidth(x);
        stage2.centerOnScreen();
    }
}
