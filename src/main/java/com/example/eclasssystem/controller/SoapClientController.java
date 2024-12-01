package com.example.eclasssystem.controller;

import com.example.eclasssystem.services.MNBService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Map;

public class SoapClientController {
    @FXML private ComboBox<String> currencyComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextArea ratesDisplayArea;
    @FXML private LineChart<String, Number> exchangeRateChart;
    @FXML private ProgressBar progressBar;

    private final MNBService mnbService = new MNBService();

    @FXML
    public void initialize() {
        // Initialize currency combo box
        currencyComboBox.getItems().addAll(
                "EUR", "USD", "HUF", "GBP", "CHF", "JPY", "AUD", "CAD", "SEK", "NOK", "DKK"
        );

        // Set default dates
        startDatePicker.setValue(LocalDate.now().minusMonths(1));
        endDatePicker.setValue(LocalDate.now());

        // Initialize progress bar
        progressBar.setVisible(false);
    }

    @FXML
    public void handleDownloadAllRates() {
        // Reset UI
        ratesDisplayArea.clear();
        exchangeRateChart.getData().clear();
        progressBar.setVisible(true);
        progressBar.setProgress(0);

        // Background thread for downloading
        new Thread(() -> {
            try {
                // Simulate progress
                updateProgress(0.3);

                // Download and process rates
                String rates = mnbService.downloadAllRates();

                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    ratesDisplayArea.setText(rates);
                    updateProgress(1.0);
                    showAlert("Success", "All rates downloaded successfully!");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    showAlert("Error", "Download failed: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    public void handleDownloadSelectedRates() {
        // Validate inputs
        String currency = currencyComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (currency == null || startDate == null || endDate == null) {
            showAlert("Error", "Please select currency and dates");
            return;
        }

        // Reset UI
        ratesDisplayArea.clear();
        exchangeRateChart.getData().clear();
        progressBar.setVisible(true);
        progressBar.setProgress(0);

        // Background thread for downloading
        new Thread(() -> {
            try {
                // Simulate progress
                updateProgress(0.3);

                // Download and process rates
                Map<LocalDate, Double> rates = mnbService.downloadSelectedRates(
                        currency, startDate, endDate);

                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    displayRates(rates);
                    updateChart(rates);
                    updateProgress(1.0);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    showAlert("Error", "Download failed: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    public void openGraphForm() {
        // Validate inputs
        String currency = currencyComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (currency == null || startDate == null || endDate == null) {
            showAlert("Error", "Please select currency and dates");
            return;
        }

        // Reset chart
        exchangeRateChart.getData().clear();

        // Background thread for graph generation
        new Thread(() -> {
            try {
                // Download selected rates
                Map<LocalDate, Double> rates = mnbService.downloadSelectedRates(
                        currency, startDate, endDate);

                // Update chart on JavaFX thread
                Platform.runLater(() -> {
                    updateChart(rates);
                    showAlert("Graph", "Graph generated successfully!");
                });
            } catch (Exception e) {
                Platform.runLater(() ->
                        showAlert("Error", "Graph generation failed: " + e.getMessage())
                );
            }
        }).start();
    }

    // Helper method to update progress bar
    private void updateProgress(double progress) {
        Platform.runLater(() -> progressBar.setProgress(progress));
    }

    // Display rates in text area
    private void displayRates(Map<LocalDate, Double> rates) {
        StringBuilder sb = new StringBuilder();
        rates.forEach((date, rate) ->
                sb.append(String.format("%s: %.2f HUF\n", date, rate)));
        ratesDisplayArea.setText(sb.toString());
    }

    // Update chart with rates
    private void updateChart(Map<LocalDate, Double> rates) {
        exchangeRateChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(currencyComboBox.getValue() + " Exchange Rates");

        rates.forEach((date, rate) ->
                series.getData().add(new XYChart.Data<>(date.toString(), rate)));

        exchangeRateChart.getData().add(series);
    }

    // Show alert dialog
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}