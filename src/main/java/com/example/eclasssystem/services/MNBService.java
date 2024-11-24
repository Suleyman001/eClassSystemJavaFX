package com.example.eclasssystem.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MNBService {
    private static final String EXCEL_URL = "https://www.mnb.hu/Root/ExchangeRate/arfolyam.xlsx";
    private static final String BANK_FILE = "src/main/resources/bank.txt";
    private static final String TEMP_EXCEL = "temp_rates.xlsx";

    public String downloadAllRates() throws Exception {
        // Download Excel file
        downloadExcelFile();

        // Read and process the file
        List<ExchangeRate> rates = readExcelFile();

        // Format and save to bank.txt
        String formattedRates = formatRates(rates);
        saveToFile(formattedRates);

        // Clean up
        new File(TEMP_EXCEL).delete();

        return formattedRates;
    }

    public Map<LocalDate, Double> downloadSelectedRates(String currency, LocalDate startDate, LocalDate endDate)
            throws Exception {
        // Download Excel file
        downloadExcelFile();

        // Read and filter data
        List<ExchangeRate> allRates = readExcelFile();
        Map<LocalDate, Double> filteredRates = new TreeMap<>();

        for (ExchangeRate rate : allRates) {
            if (rate.getCurrency().equals(currency) &&
                    !rate.getDate().isBefore(startDate) &&
                    !rate.getDate().isAfter(endDate) &&
                    rate.getRate() > 0) {
                filteredRates.put(rate.getDate(), rate.getRate());
            }
        }

        // Clean up
        new File(TEMP_EXCEL).delete();

        return filteredRates;
    }

    private void downloadExcelFile() throws Exception {
        URL website = new URL(EXCEL_URL);
        try (ReadableByteChannel rbc = Channels.newChannel(website.openStream());
             FileOutputStream fos = new FileOutputStream(TEMP_EXCEL)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }

    private List<ExchangeRate> readExcelFile() throws Exception {
        List<ExchangeRate> rates = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(new File(TEMP_EXCEL))) {
            Sheet sheet = workbook.getSheetAt(0);

            // Get currency columns from header row
            Map<Integer, String> currencyColumns = new HashMap<>();
            Row headerRow = sheet.getRow(0);
            for (int i = 1; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell != null) {
                    currencyColumns.put(i, cell.getStringCellValue());
                }
            }

            // Read data rows
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd.");
            for (int i = 2; i < sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    String dateStr = row.getCell(0).getStringCellValue();
                    LocalDate date = LocalDate.parse(dateStr, formatter);

                    for (Map.Entry<Integer, String> entry : currencyColumns.entrySet()) {
                        Cell cell = row.getCell(entry.getKey());
                        if (cell != null) {
                            double rate = 0;
                            try {
                                rate = cell.getNumericCellValue();
                            } catch (IllegalStateException e) {
                                // Skip if cell is not numeric
                                continue;
                            }

                            if (rate > 0) {
                                rates.add(new ExchangeRate(date, entry.getValue(), rate));
                            }
                        }
                    }
                }
            }
        }

        return rates;
    }

    private String formatRates(List<ExchangeRate> rates) {
        StringBuilder sb = new StringBuilder();
        sb.append("MNB Exchange Rates\n");
        sb.append("Downloaded on: ").append(LocalDate.now()).append("\n\n");

        rates.forEach(rate ->
                sb.append(String.format("%s: 1 %s = %.2f HUF\n",
                        rate.getDate(), rate.getCurrency(), rate.getRate())));

        return sb.toString();
    }

    private void saveToFile(String content) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BANK_FILE))) {
            writer.println(content);
        }
    }

    // Helper class to store rate information
    private static class ExchangeRate {
        private final LocalDate date;
        private final String currency;
        private final double rate;

        public ExchangeRate(LocalDate date, String currency, double rate) {
            this.date = date;
            this.currency = currency;
            this.rate = rate;
        }

        public LocalDate getDate() { return date; }
        public String getCurrency() { return currency; }
        public double getRate() { return rate; }
    }
}