package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    private static Stage stage2;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("GUI.fxml"));
        stage2=primaryStage;
        stage2.setTitle("CouchDB Data Mining");
        stage2.setResizable(false);
        stage2.setScene(new Scene(root));
        stage2.show();
    }


    public static void main(String[] args) {
        
        launch(args);
    }
    
    public static void resize(double x, double y){
        
        stage2.setHeight(y);
        stage2.setWidth(x);
        stage2.centerOnScreen();
    }
}
