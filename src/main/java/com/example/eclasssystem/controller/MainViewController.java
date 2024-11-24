package com.example.eclasssystem.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.scene.control.Alert;
import java.io.IOException;

public class MainViewController {
    private static final Logger logger = LoggerFactory.getLogger(MainViewController.class);
    @FXML
    public void openStudentsView() {
        try {
            Parent studentsView = FXMLLoader.load(getClass().getResource("/fxml/StudentsView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Students");
            stage.setScene(new Scene(studentsView));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openSubjectsView() {
        try {
            Parent subjectsView = FXMLLoader.load(getClass().getResource("/fxml/SubjectsView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Subjects");
            stage.setScene(new Scene(subjectsView));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openMarksView() {
        try {
            Parent marksView = FXMLLoader.load(getClass().getResource("/fxml/MarksView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Marks");
            stage.setScene(new Scene(marksView));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openStatisticsView() {
        try {
            Parent statisticsView = FXMLLoader.load(getClass().getResource("/fxml/StatisticsView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Statistics");
            stage.setScene(new Scene(statisticsView));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openSoapClientView() {
        try {
            Parent soapClientView = FXMLLoader.load(getClass().getResource("/fxml/SoapClientView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("MNB Exchange Rates");
            stage.setScene(new Scene(soapClientView, 600, 500));
            stage.show();
        } catch (IOException e) {
            logger.error("Error opening SOAP Client view", e);
            // Optionally show an error dialog
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("View Error");
            alert.setHeaderText("Could not open SOAP Client view");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}