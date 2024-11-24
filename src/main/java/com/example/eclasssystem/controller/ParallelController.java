package com.example.eclasssystem.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelController {
    @FXML private Label label1;
    @FXML private Label label2;
    @FXML private Label statusLabel;
    @FXML private Button startButton;
    @FXML private Button stopButton;

    private ExecutorService executorService;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicInteger counter1 = new AtomicInteger(0);
    private final AtomicInteger counter2 = new AtomicInteger(0);

    @FXML
    private void handleStart() {
        // Initialize counters
        counter1.set(0);
        counter2.set(0);

        // Update UI
        startButton.setDisable(true);
        stopButton.setDisable(false);
        statusLabel.setText("Status: Running");
        statusLabel.setStyle("-fx-text-fill: green;");

        // Start parallel processing
        isRunning.set(true);
        executorService = Executors.newFixedThreadPool(2);

        // Counter 1 - updates every 1 second
        executorService.submit(() -> {
            while (isRunning.get()) {
                try {
                    Thread.sleep(1000);
                    Platform.runLater(() ->
                            label1.setText(String.valueOf(counter1.incrementAndGet()))
                    );
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        // Counter 2 - updates every 2 seconds
        executorService.submit(() -> {
            while (isRunning.get()) {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() ->
                            label2.setText(String.valueOf(counter2.incrementAndGet()))
                    );
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    @FXML
    private void handleStop() {
        // Stop the counters
        isRunning.set(false);

        // Shutdown executor service
        if (executorService != null) {
            executorService.shutdown();
        }

        // Update UI
        startButton.setDisable(false);
        stopButton.setDisable(true);
        statusLabel.setText("Status: Stopped");
        statusLabel.setStyle("-fx-text-fill: red;");
    }

    public void initialize() {
        // Initial setup
        label1.setText("0");
        label2.setText("0");
        stopButton.setDisable(true);
    }
}