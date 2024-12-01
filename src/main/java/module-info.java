module com.example.eclasssystem {
    // JavaFX Modules
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    // Logging Modules
    requires org.apache.logging.log4j;
    requires org.slf4j;

    // Database
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    // HTTP and Network
    requires okhttp3;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires java.net.http;

    // File Processing
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires com.opencsv;
    requires itextpdf;

    // XML Processing
    requires org.jsoup;
    requires xerces;

    // Reflection and Runtime
    requires java.desktop;

    // Export and Open Packages
    exports com.example.eclasssystem;
    exports com.example.eclasssystem.controller;
    exports com.example.eclasssystem.services;
    exports com.example.eclasssystem.util;
    exports com.example.eclasssystem.exceptions;
    exports com.example.eclasssystem.mnbdownloader;

    // Open packages for reflection and JavaFX
    opens com.example.eclasssystem to javafx.fxml;
    opens com.example.eclasssystem.controller to javafx.fxml;
    opens com.example.eclasssystem.services to javafx.base;
    opens com.example.eclasssystem.mnbdownloader to javafx.base;
    opens com.example.eclasssystem.util to javafx.base;
}