package com.example.eclasssystem;

import com.example.eclasssystem.services.DatabaseManager;
import com.example.eclasssystem.util.DeploymentUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.*;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("Running from JAR: " + DeploymentUtil.isRunningFromJar());
            System.out.println("Application Home: " + DeploymentUtil.getApplicationHomeDirectory());

            DatabaseManager dbManager = new DatabaseManager();
            dbManager.initializeDatabase();

            // Use resource streams for importing
            try (InputStream studentsStream = getClass().getResourceAsStream("/txt/students.txt");
                 InputStream subjectsStream = getClass().getResourceAsStream("/txt/subjects.txt");
                 InputStream marksStream = getClass().getResourceAsStream("/txt/marks.txt")) {

                dbManager.importStudentsFromTxt(studentsStream);
                dbManager.importSubjectsFromTxt(subjectsStream);
                dbManager.importMarksFromTxt(marksStream);
            }

            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainView.fxml"));
            Scene scene = new Scene(root, 400, 500);



            primaryStage.setTitle("EClass System");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog(e);
        }
    }
    // Error dialog method
    private void showErrorDialog(Throwable e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Application Error");
        alert.setHeaderText("An unexpected error occurred");
        alert.setContentText(e.getMessage());

        // Create expandable Exception
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }



}