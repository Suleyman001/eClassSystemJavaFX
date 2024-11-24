package com.example.eclasssystem.controller;

import com.example.eclasssystem.services.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

public class StatisticsViewController {
    @FXML
    private Label totalStudentsLabel;
    @FXML
    private Label totalSubjectsLabel;
    @FXML
    private Label totalMarksLabel;
    @FXML
    private BarChart<String, Number> averageMarksChart;

    private DatabaseManager dbManager;

    @FXML
    public void initialize() {
        dbManager = new DatabaseManager();

        // Update summary labels
        updateSummaryLabels();

        // Generate charts
        generateAverageMarksChart();
    }

    private void updateSummaryLabels() {
        totalStudentsLabel.setText("Total Students: " + dbManager.getTableRecordCount("students"));
        totalSubjectsLabel.setText("Total Subjects: " + dbManager.getTableRecordCount("subjects"));
        totalMarksLabel.setText("Total Marks: " + dbManager.getTableRecordCount("marks"));
    }

    private void generateAverageMarksChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Average Marks by Subject");

        dbManager.readAllSubjects().forEach(subject -> {
            double avgMark = dbManager.getAverageMarkBySubject(subject.getId());
            series.getData().add(new XYChart.Data<>(subject.getName(), avgMark));
        });

        averageMarksChart.getData().add(series);
    }
}