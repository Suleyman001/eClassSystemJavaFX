package com.example.eclasssystem.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainViewController {

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
}