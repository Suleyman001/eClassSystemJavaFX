package com.example.eclasssystem.controller;

import com.example.eclasssystem.services.MNBService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.util.Map;

public class SoapClientController {
    @FXML private ComboBox<String> currencyComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextArea ratesDisplayArea;
    @FXML private LineChart<String, Number> exchangeRateChart;

    private final MNBService mnbService = new MNBService();

    @FXML
    public void initialize() {
        // Initialize currency combo box
        currencyComboBox.getItems().addAll(
                "EUR", "USD", "GBP", "CHF", "JPY", "AUD", "CAD", "SEK", "NOK", "DKK"
        );

        // Set default dates
        startDatePicker.setValue(LocalDate.now().minusMonths(1));
        endDatePicker.setValue(LocalDate.now());
    }

    @FXML
    private void handleDownloadAllRates() {
        new Thread(() -> {
            try {
                String rates = mnbService.downloadAllRates();
                Platform.runLater(() -> {
                    ratesDisplayArea.setText(rates);
                    showAlert("Success", "All rates downloaded successfully!");
                });
            } catch (Exception e) {
                Platform.runLater(() -> showAlert("Error", e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void handleDownloadSelectedRates() {
        String currency = currencyComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (currency == null || startDate == null || endDate == null) {
            showAlert("Error", "Please select currency and dates");
            return;
        }

        new Thread(() -> {
            try {
                Map<LocalDate, Double> rates = mnbService.downloadSelectedRates(
                        currency, startDate, endDate);

                Platform.runLater(() -> {
                    displayRates(rates);
                    updateChart(rates);
                });
            } catch (Exception e) {
                Platform.runLater(() -> showAlert("Error", e.getMessage()));
            }
        }).start();
    }

    private void displayRates(Map<LocalDate, Double> rates) {
        StringBuilder sb = new StringBuilder();
        rates.forEach((date, rate) ->
                sb.append(String.format("%s: %.2f HUF\n", date, rate)));
        ratesDisplayArea.setText(sb.toString());
    }

    private void updateChart(Map<LocalDate, Double> rates) {
        exchangeRateChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        rates.forEach((date, rate) ->
                series.getData().add(new XYChart.Data<>(date.toString(), rate)));

        exchangeRateChart.getData().add(series);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}