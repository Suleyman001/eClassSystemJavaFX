module com.example.eclasssystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // Simplified SLF4J requirements
    requires org.slf4j;
    requires static java.logging;

    requires org.xerial.sqlitejdbc;
    requires com.opencsv;
    requires org.apache.poi.ooxml;
    requires itextpdf;


    requires java.net.http;
    requires org.jsoup;

    // Export and open necessary packages
    exports com.example.eclasssystem;
    exports com.example.eclasssystem.controller;
    exports com.example.eclasssystem.services;
    exports com.example.eclasssystem.util;
    exports com.example.eclasssystem.exceptions;

    opens com.example.eclasssystem to javafx.fxml;
    opens com.example.eclasssystem.controller to javafx.fxml;
    opens com.example.eclasssystem.services to javafx.base;


    opens com.example.eclasssystem.mnbdownloader to javafx.base;
}