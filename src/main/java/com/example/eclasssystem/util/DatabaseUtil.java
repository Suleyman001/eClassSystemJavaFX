package com.example.eclasssystem.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.*;

public class DatabaseUtil {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtil.class);

    public static Path extractDatabaseFile() {
        try {
            // First, check if database exists in the current directory
            Path localDbPath = Paths.get("data.db");
            if (Files.exists(localDbPath)) {
                logger.info("Using local database: {}", localDbPath);
                return localDbPath;
            }

            // If not, try to extract from resources
            URL dbUrl = DatabaseUtil.class.getResource("/database/data.db");
            if (dbUrl == null) {
                logger.error("Database file not found in resources");
                return null;
            }

            // Create application home directory
            Path homeDir = Paths.get(System.getProperty("user.home"), ".eClassSystem");
            Files.createDirectories(homeDir);

            // Define the destination path
            Path destDbPath = homeDir.resolve("data.db");

            // Copy the database file if it doesn't exist
            if (!Files.exists(destDbPath)) {
                try (InputStream is = dbUrl.openStream()) {
                    Files.copy(is, destDbPath, StandardCopyOption.REPLACE_EXISTING);
                    logger.info("Database file extracted to: {}", destDbPath);
                }
            }

            return destDbPath;
        } catch (IOException e) {
            logger.error("Error extracting database file", e);
            return null;
        }
    }

    public static String getDatabaseUrl() {
        Path dbPath = extractDatabaseFile();
        return dbPath != null
                ? "jdbc:sqlite:" + dbPath.toString()
                : "jdbc:sqlite:data.db";
    }
}