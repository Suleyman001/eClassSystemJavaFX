package com.example.eclasssystem.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.logging.Level;

public class DeploymentUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentUtil.class);

    /**
     * Copies a resource from the JAR to a temporary file
     * @param resourcePath Path of the resource inside JAR
     * @return Path to the extracted temporary file
     */
    public static Path extractResourceToTempFile(String resourcePath) {
        try {
            // Get the resource URL
            URL resourceUrl = DeploymentUtil.class.getResource(resourcePath);

            if (resourceUrl == null) {
                LOGGER.warn("Resource not found: {}", resourcePath);
                return null;
            }

            // Create a temporary file in a specific directory
            Path tempDir = Paths.get(System.getProperty("user.home"), ".eClassSystem", "temp");
            Files.createDirectories(tempDir);

            Path tempFile = Files.createTempFile(tempDir, "extracted_",
                    getFileExtension(resourcePath));
            tempFile.toFile().deleteOnExit();

            // Copy the resource to the temporary file
            try (InputStream is = resourceUrl.openStream()) {
                Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }

            LOGGER.info("Extracted resource to: {}", tempFile);
            return tempFile;
        } catch (IOException e) {
            LOGGER.error("Error extracting resource", e);
            return null;
        }
    }

    /**
     * Get file extension from resource path
     */
    private static String getFileExtension(String resourcePath) {
        int dotIndex = resourcePath.lastIndexOf('.');
        return (dotIndex == -1) ? ".tmp" : resourcePath.substring(dotIndex);
    }

    /**
     * Checks if running from JAR
     */
    public static boolean isRunningFromJar() {
        try {
            URL location = DeploymentUtil.class.getProtectionDomain().getCodeSource().getLocation();
            return location.toString().endsWith(".jar");
        } catch (Exception e) {
            LOGGER.error("Error checking JAR status", e);
            return false;
        }
    }

    /**
     * Get application home directory
     */
    public static Path getApplicationHomeDirectory() {
        Path homeDir;
        if (isRunningFromJar()) {
            // For JAR, use user's home directory
            homeDir = Paths.get(System.getProperty("user.home"), ".eClassSystem");
        } else {
            // For development, use current directory
            homeDir = Paths.get(".");
        }

        // Ensure directory exists
        try {
            Files.createDirectories(homeDir);
        } catch (IOException e) {
            LOGGER.error("Error creating home directory", e);
        }

        return homeDir;
    }

    /**
     * Safely copy a resource to a destination
     * @param resourcePath Source resource path
     * @param destinationPath Destination path
     * @return true if successful, false otherwise
     */
    public static boolean copyResourceToFile(String resourcePath, Path destinationPath) {
        try {
            URL resourceUrl = DeploymentUtil.class.getResource(resourcePath);

            if (resourceUrl == null) {
                LOGGER.warn("Resource not found: {}", resourcePath);
                return false;
            }

            // Ensure parent directory exists
            Files.createDirectories(destinationPath.getParent());

            // Copy the resource
            try (InputStream is = resourceUrl.openStream()) {
                Files.copy(is, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            }

            LOGGER.info("Copied resource from {} to {}", resourcePath, destinationPath);
            return true;
        } catch (IOException e) {
            LOGGER.error("Error copying resource", e);
            return false;
        }
    }

    /**
     * Get a unique temporary file
     * @param prefix File name prefix
     * @param suffix File extension
     * @return Path to the temporary file
     */
    public static Path createTempFile(String prefix, String suffix) {
        try {
            Path tempDir = Paths.get(System.getProperty("user.home"), ".eClassSystem", "temp");
            Files.createDirectories(tempDir);

            Path tempFile = Files.createTempFile(tempDir, prefix, suffix);
            tempFile.toFile().deleteOnExit();

            return tempFile;
        } catch (IOException e) {
            LOGGER.error("Error creating temporary file", e);
            return null;
        }
    }

    /**
     * Print system and deployment information
     */
    public static void printDeploymentInfo() {
        LOGGER.info("Deployment Information:");
        LOGGER.info("Running from JAR: {}", isRunningFromJar());
        LOGGER.info("Application Home Directory: {}", getApplicationHomeDirectory());
        LOGGER.info("User Home: {}", System.getProperty("user.home"));
        LOGGER.info("Working Directory: {}", System.getProperty("user.dir"));
        LOGGER.info("Java Version: {}", System.getProperty("java.version"));
        LOGGER.info("Operating System: {} {}",
                System.getProperty("os.name"),
                System.getProperty("os.version")
        );
    }
}