package com.example.eclasssystem.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MNBService {
    private static final Logger LOGGER = Logger.getLogger(MNBService.class.getName());
    private static final String EXCEL_URL = "https://www.mnb.hu/Root/ExchangeRate/arfolyam.xlsx";
    private static final String BANK_FILE = "exchange_rates.txt";
    private static final String TEMP_EXCEL = "temp_rates.xlsx";

    static {
        try {
            configureSSLCertificates();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "SSL Configuration Error", e);
        }
    }

    // Comprehensive SSL Configuration Method
    private static void configureSSLCertificates() throws NoSuchAlgorithmException, KeyManagementException {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };
        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = (hostname, session) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    public String downloadAllRates() throws Exception {
        try {
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
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Download All Rates Failed", e);
            throw e;
        }
    }

    // Helper class to store rate information
    public static class ExchangeRate {
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

        @Override
        public String toString() {
            return String.format("%s - %s: %.4f",
                    date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    currency,
                    rate
            );
        }
    }

    // Additional Logging Method
    private void logDownloadDetails(Exception e) {
        LOGGER.log(Level.SEVERE, "Detailed Download Error", e);

        // Additional system information logging
        LOGGER.info("Java Version: " + System.getProperty("java.version"));
        LOGGER.info("OS Name: " + System.getProperty("os.name"));
        LOGGER.info("User Home: " + System.getProperty("user.home"));
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
        try {
            URL website = new URL(EXCEL_URL);
            HttpsURLConnection connection = (HttpsURLConnection) website.openConnection();

            // Comprehensive Connection Configuration
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);  // 15 seconds
            connection.setReadTimeout(30000);     // 30 seconds
            connection.setDoInput(true);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            // Log connection details
            LOGGER.info("Connecting to: " + EXCEL_URL);
            LOGGER.info("Response Code: " + connection.getResponseCode());

            // Download file
            try (InputStream inputStream = connection.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(TEMP_EXCEL)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalBytesRead = 0;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                }

                LOGGER.info("Downloaded " + totalBytesRead + " bytes");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Download Failed", e);
            throw new RuntimeException("Unable to download Excel file: " + e.getMessage(), e);
        }
    }

    private List<ExchangeRate> readExcelFile() throws Exception {
        List<ExchangeRate> rates = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(new File(TEMP_EXCEL))) {
            Sheet sheet = workbook.getSheetAt(0);

            // Prepare currency columns mapping
            Map<Integer, String> currencyColumns = new HashMap<>();
            Row headerRow = sheet.getRow(0);
            Row unitRow = sheet.getRow(1);

            // Skip first column (Date)
            for (int i = 1; i < headerRow.getLastCellNum(); i++) {
                Cell currencyCell = headerRow.getCell(i);
                if (currencyCell != null) {
                    String currencyName = getCellValueAsString(currencyCell);
                    if (currencyName != null && !currencyName.isEmpty()) {
                        currencyColumns.put(i, currencyName);
                    }
                }
            }

            // Date formatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd.");

            // Process data rows starting from row 2 (index 2)
            for (int rowIndex = 2; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                // Extract date
                Cell dateCell = row.getCell(0);
                if (dateCell == null) continue;

                String dateStr = getCellValueAsString(dateCell);
                if (dateStr == null || dateStr.isEmpty()) continue;

                LocalDate date;
                try {
                    date = LocalDate.parse(dateStr, formatter);
                } catch (Exception e) {
                    LOGGER.warning("Could not parse date: " + dateStr);
                    continue;
                }

                // Process rates for each currency
                for (Map.Entry<Integer, String> entry : currencyColumns.entrySet()) {
                    Cell rateCell = row.getCell(entry.getKey());

                    // Default rate handling
                    double rate = 1.0; // Default to 1 if cell is empty

                    if (rateCell != null) {
                        try {
                            rate = getCellValueAsDouble(rateCell);

                            // If rate is 0, use default 1
                            if (rate == 0) {
                                rate = 1.0;
                            }
                        } catch (Exception e) {
                            // If parsing fails, use default 1
                            rate = 1.0;
                            LOGGER.warning("Could not parse rate for " + entry.getValue() +
                                    " on " + date + ". Using default value.");
                        }
                    }

                    // Add rate to list
                    rates.add(new ExchangeRate(date, entry.getValue(), rate));
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Detailed Error reading Excel file: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        // Sort rates by date
        rates.sort(Comparator.comparing(ExchangeRate::getDate));

        return rates;
    }

    // Robust cell value extraction methods
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;

        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue().trim();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        // Format date if it's a date cell
                        return new SimpleDateFormat("yyyy. MM. dd.")
                                .format(cell.getDateCellValue());
                    }
                    // Convert numeric to string, removing decimal for whole numbers
                    double numValue = cell.getNumericCellValue();
                    return numValue == (long) numValue ?
                            String.format("%d", (long) numValue) :
                            String.valueOf(numValue);
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    // Evaluate formula cell
                    try {
                        return cell.getStringCellValue().trim();
                    } catch (Exception e) {
                        return String.valueOf(cell.getNumericCellValue());
                    }
                default:
                    return null;
            }
        } catch (Exception e) {
            LOGGER.warning("Error extracting cell value: " + e.getMessage());
            return null;
        }
    }

    private double getCellValueAsDouble(Cell cell) {
        if (cell == null) return 1.0;

        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return cell.getNumericCellValue();
                case STRING:
                    try {
                        // Handle comma and dot decimal separators
                        String cleanValue = cell.getStringCellValue()
                                .replace(',', '.')
                                .trim()
                                .replaceAll("[^0-9.]", "");
                        return cleanValue.isEmpty() ? 1.0 : Double.parseDouble(cleanValue);
                    } catch (NumberFormatException e) {
                        LOGGER.warning("Could not parse string to double: " +
                                cell.getStringCellValue());
                        return 1.0;
                    }
                case FORMULA:
                    try {
                        return cell.getNumericCellValue();
                    } catch (Exception e) {
                        LOGGER.warning("Could not get numeric value from formula cell");
                        return 1.0;
                    }
                default:
                    return 1.0;
            }
        } catch (Exception e) {
            LOGGER.warning("Unexpected error parsing cell value: " + e.getMessage());
            return 1.0;
        }
    }



    private String formatRates(List<ExchangeRate> rates) {
        StringBuilder sb = new StringBuilder();
        sb.append("Exchange Rates:\n");
        sb.append("----------------\n");

        // Group by date
        Map<LocalDate, List<ExchangeRate>> ratesByDate = rates.stream()
                .collect(Collectors.groupingBy(ExchangeRate::getDate));

        // Sort dates
        List<LocalDate> sortedDates = new ArrayList<>(ratesByDate.keySet());
        Collections.sort(sortedDates);

        for (LocalDate date : sortedDates) {
            sb.append(date.format(DateTimeFormatter.ISO_LOCAL_DATE)).append(":\n");
            ratesByDate.get(date).forEach(rate ->
                    sb.append(String.format("  %s: %.4f\n", rate.getCurrency(), rate.getRate()))
            );
            sb.append("\n");
        }

        return sb.toString();
    }

    private void saveToFile(String content) throws IOException {
        File file = new File(BANK_FILE);
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println(content);
            LOGGER.info("Exchange rates saved to " + file.getAbsolutePath());
        }
    }


}