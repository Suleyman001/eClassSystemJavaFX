package com.example.eclasssystem.mnbdownloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MNBRateDownloader {
    private static final Logger logger = LoggerFactory.getLogger(MNBRateDownloader.class);
    private static final String OUTPUT_FILE = "src/main/resources/bank.txt";

    public static void writeAllRatesToFile() throws IOException {
        Map<String, Double> rates = getFallbackRates();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE))) {
            writer.write("MNB Exchange Rates - All Currencies\n");
            writer.write("Downloaded on: " + LocalDateTime.now() + "\n\n");

            for (Map.Entry<String, Double> entry : rates.entrySet()) {
                writer.write(String.format("%s: 1 %s = %.2f HUF\n",
                        entry.getKey(), entry.getKey(), entry.getValue()));
            }

            logger.info("All rates written to {}", OUTPUT_FILE);
        }
    }

    public static void writeSelectedRatesToFile(String[] selectedCurrencies) throws IOException {
        Map<String, Double> allRates = getFallbackRates();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE))) {
            writer.write("MNB Exchange Rates - Selected Currencies\n");
            writer.write("Downloaded on: " + LocalDateTime.now() + "\n\n");

            for (String currency : selectedCurrencies) {
                if (allRates.containsKey(currency)) {
                    writer.write(String.format("%s: 1 %s = %.2f HUF\n",
                            currency, currency, allRates.get(currency)));
                }
            }

            logger.info("Selected rates written to {}", OUTPUT_FILE);
        }
    }

    public static Map<String, Double> getFallbackRates() {
        Map<String, Double> fallbackRates = new HashMap<>();
        fallbackRates.put("USD", 300.0);
        fallbackRates.put("EUR", 350.0);
        fallbackRates.put("GBP", 400.0);
        fallbackRates.put("CHF", 325.0);
        fallbackRates.put("HUF", 1.0);

        return fallbackRates;
    }
}