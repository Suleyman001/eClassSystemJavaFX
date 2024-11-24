package com.example.eclasssystem;

import com.example.eclasssystem.services.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            DatabaseManager dbManager = new DatabaseManager();
            dbManager.initializeDatabase();
            dbManager.importStudentsFromTxt("src/main/resources/txt/students.txt");
            dbManager.importSubjectsFromTxt("src/main/resources/txt/subjects.txt");
            dbManager.importMarksFromTxt("src/main/resources/txt/marks.txt");

            // Ensure the FXML path is correct
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainView.fxml"));
            Scene scene = new Scene(root, 400, 500);
            primaryStage.setTitle("EClass System");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }



}