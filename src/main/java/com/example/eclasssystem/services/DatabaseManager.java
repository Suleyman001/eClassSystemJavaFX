package com.example.eclasssystem.services;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;
import java.net.URL;

import com.example.eclasssystem.util.DatabaseUtil;
import com.example.eclasssystem.util.DeploymentUtil;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class DatabaseManager {
    private static String DB_URL = DatabaseUtil.getDatabaseUrl();
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    // Database Connection Method
    public static Connection connect() throws SQLException {
        try {
            // Ensure the database file is extracted
            DatabaseUtil.extractDatabaseFile();

            // Connect to the database
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            logger.error("Database connection error", e);
            throw e;
        }
    }

    // Database Initialization Method
    public void initializeDatabase() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            // Drop existing tables to ensure clean slate
            stmt.execute("DROP TABLE IF EXISTS marks");
            stmt.execute("DROP TABLE IF EXISTS students");
            stmt.execute("DROP TABLE IF EXISTS subjects");

            // Create Students Table with AUTOINCREMENT
            stmt.execute("CREATE TABLE students (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "sname TEXT NOT NULL," +
                    "class TEXT NOT NULL," +
                    "boy INTEGER NOT NULL)");

            // Create Subjects Table with AUTOINCREMENT
            stmt.execute("CREATE TABLE subjects (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "sname TEXT NOT NULL," +
                    "category TEXT NOT NULL)");

            // Create Marks Table
            stmt.execute("CREATE TABLE marks (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "studentid INTEGER NOT NULL," +
                    "mdate TEXT NOT NULL," +
                    "mark INTEGER NOT NULL," +
                    "type TEXT NOT NULL," +
                    "subjectid INTEGER NOT NULL," +
                    "FOREIGN KEY(studentid) REFERENCES students(id)," +
                    "FOREIGN KEY(subjectid) REFERENCES subjects(id))");

            logger.info("Database initialized successfully");
        } catch (SQLException e) {
            logger.error("Error initializing database", e);
            showErrorAlert("Database Initialization Error",
                    "Could not create database tables",
                    e.getMessage());
        }
    }

    // Import Methods for Students, Subjects, and Marks (as in previous message)
    public void importStudentsFromTxt(InputStream inputStream) {
        logger.info("Starting import of students from input stream");
        try (Connection conn = connect();
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO students (sname, class, boy) VALUES (?, ?, ?)")) {

            // Skip header
            br.readLine();

            String line;
            int importedCount = 0;
            while ((line = br.readLine()) != null) {
                try {
                    String[] data = line.split("\t");

                    // Validate data before insertion
                    if (data.length < 4) {
                        logger.warn("Skipping invalid line: {}", line);
                        continue;
                    }

                    pstmt.setString(1, data[1]);                    // sname
                    pstmt.setString(2, data[2]);                    // class
                    pstmt.setInt(3, data[3].equals("-1") ? 0 : 1); // boy

                    pstmt.executeUpdate();
                    importedCount++;
                } catch (Exception e) {
                    logger.error("Error processing line: {}", line, e);
                }
            }

            logger.info("Students imported successfully: {}", importedCount);

        } catch (SQLException | IOException e) {
            logger.error("Critical error during student import", e);
            showErrorAlert("Import Error",
                    "Could not import students",
                    e.getMessage());
        }
    }

    public void importSubjectsFromTxt(InputStream inputStream) {
        logger.info("Starting import of subjects from input stream");
        try (Connection conn = connect();
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO subjects (sname, category) VALUES (?, ?)")) {

            // Skip header
            br.readLine();

            String line;
            int importedCount = 0;
            while ((line = br.readLine()) != null) {
                try {
                    String[] data = line.split("\t");

                    // Validate data before insertion
                    if (data.length < 3) {
                        logger.warn("Skipping invalid line: {}", line);
                        continue;
                    }

                    pstmt.setString(1, data[1]);                 // sname
                    pstmt.setString(2, data[2]);                 // category

                    pstmt.executeUpdate();
                    importedCount++;
                } catch (Exception e) {
                    logger.error("Error processing line: {}", line, e);
                }
            }

            logger.info("Subjects imported successfully: {}", importedCount);

        } catch (SQLException | IOException e) {
            logger.error("Critical error during subjects import", e);
            showErrorAlert("Import Error",
                    "Could not import subjects",
                    e.getMessage());
        }
    }

    public void importMarksFromTxt(InputStream inputStream) {
        logger.info("Starting import of marks from input stream");
        try (Connection conn = connect();
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO marks (studentid, mdate, mark, type, subjectid) VALUES (?, ?, ?, ?, ?)")) {

            // Skip header
            br.readLine();

            String line;
            int importedCount = 0;
            while ((line = br.readLine()) != null) {
                try {
                    String[] data = line.split("\t");

                    // Validate data before insertion
                    if (data.length < 5) {
                        logger.warn("Skipping invalid line: {}", line);
                        continue;
                    }

                    pstmt.setInt(1, Integer.parseInt(data[0]));     // studentid
                    pstmt.setString(2, data[1]);                    // mdate
                    pstmt.setInt(3, Integer.parseInt(data[2]));     // mark
                    pstmt.setString(4, data[3]);                    // type
                    pstmt.setInt(5, Integer.parseInt(data[4]));     // subjectid

                    pstmt.executeUpdate();
                    importedCount++;
                } catch (Exception e) {
                    logger.error("Error processing line: {}", line, e);
                }
            }

            logger.info("Marks imported successfully: {}", importedCount);

        } catch (SQLException | IOException e) {
            logger.error("Critical error during marks import", e);
            showErrorAlert("Import Error",
                    "Could not import marks",
                    e.getMessage());
        }
    }

    // Error Alert Method
    private void showErrorAlert(String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    // Student CRUD Operations
    public void createStudent(String name, String className, boolean isBoy) {
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO students (sname, class, boy) VALUES (?, ?, ?)")) {
            pstmt.setString(1, name);
            pstmt.setString(2, className);
            pstmt.setInt(3, isBoy ? 1 : 0);
            pstmt.executeUpdate();

            logger.info("Student created: {} in class {}", name, className);
        } catch (SQLException e) {
            logger.error("Error creating student", e);
            showErrorAlert("Database Error",
                    "Could not create student",
                    e.getMessage());
        }
    }

    public List<Student> readAllStudents() {
        List<Student> students = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM students")) {

            while (rs.next()) {
                Student student = new Student(
                        rs.getInt("id"),
                        rs.getString("sname"),
                        rs.getString("class"),
                        rs.getInt("boy") == 1
                );
                students.add(student);
            }
        } catch (SQLException e) {
            logger.error("Error reading students", e);
        }
        return students;
    }

    public Student readStudentById(int id) {
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM students WHERE id = ?")) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Student(
                        rs.getInt("id"),
                        rs.getString("sname"),
                        rs.getString("class"),
                        rs.getInt("boy") == 1
                );
            }
        } catch (SQLException e) {
            logger.error("Error reading student by ID", e);
        }
        return null;
    }

    public void updateStudent(int id, String name, String className, boolean isBoy) {
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE students SET sname = ?, class = ?, boy = ? WHERE id = ?")) {

            pstmt.setString(1, name);
            pstmt.setString(2, className);
            pstmt.setInt(3, isBoy ? 1 : 0);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();

            logger.info("Student updated: {} in class {}", name, className);
        } catch (SQLException e) {
            logger.error("Error updating student", e);
            showErrorAlert("Database Error",
                    "Could not update student",
                    e.getMessage());
        }
    }

    public void deleteStudent(int id) {
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "DELETE FROM students WHERE id = ?")) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

            logger.info("Student deleted with ID: {}", id);
        } catch (SQLException e) {
            logger.error("Error deleting student", e);
            showErrorAlert("Database Error",
                    "Could not delete student",
                    e.getMessage());
        }
    }

    // Subject CRUD Operations
    public void createSubject(String name, String category) {
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO subjects (sname, category) VALUES (?, ?)")) {

            pstmt.setString(1, name);
            pstmt.setString(2, category);
            pstmt.executeUpdate();

            logger.info("Subject created: {} in category {}", name, category);
        } catch (SQLException e) {
            logger.error("Error creating subject", e);
            showErrorAlert("Database Error",
                    "Could not create subject",
                    e.getMessage());
        }
    }

    public List<Subject> readAllSubjects() {
        List<Subject> subjects = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM subjects")) {

            while (rs.next()) {
                Subject subject = new Subject(
                        rs.getInt("id"),
                        rs.getString("sname"),
                        rs.getString("category")
                );
                subjects.add(subject);
            }
        } catch (SQLException e) {
            logger.error("Error reading subjects", e);
        }
        return subjects;
    }

    public Subject readSubjectById(int id) {
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM subjects WHERE id = ?")) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Subject(
                        rs.getInt("id"),
                        rs.getString("sname"),
                        rs.getString("category")
                );
            }
        } catch (SQLException e) {
            logger.error("Error reading subject by ID", e);
        }
        return null;
    }

    public void updateSubject(int id, String name, String category) {
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE subjects SET sname = ?, category = ? WHERE id = ?")) {

            pstmt.setString(1, name);
            pstmt.setString(2, category);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();

            logger.info("Subject updated: {} in category {}", name, category);
        } catch (SQLException e) {
            logger.error("Error updating subject", e);
            showErrorAlert("Database Error",
                    "Could not update subject",
                    e.getMessage());
        }
    }

    public void deleteSubject(int id) {
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "DELETE FROM subjects WHERE id = ?")) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

            logger.info("Subject deleted with ID: {}", id);
        } catch (SQLException e) {
            logger.error("Error deleting subject", e);
            showErrorAlert("Database Error",
                    "Could not delete subject",
                    e.getMessage());
        }
    }

    // Mark CRUD Operations
    public void createMark(int studentId, String date, int mark, String type, int subjectId) {
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO marks (studentid, mdate, mark, type, subjectid) VALUES (?, ?, ?, ?, ?)")) {

            pstmt.setInt(1, studentId);
            pstmt.setString(2, date);
            pstmt.setInt(3, mark);
            pstmt.setString(4, type);
            pstmt.setInt(5, subjectId);
            pstmt.executeUpdate();

            logger.info("Mark created for student ID: {} in subject ID: {}", studentId, subjectId);
        } catch (SQLException e) {
            logger.error("Error creating mark", e);
            showErrorAlert("Database Error",
                    "Could not create mark",
                    e.getMessage());
        }
    }

    public List<Mark> readAllMarks() {
        List<Mark> marks = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM marks")) {

            while (rs.next()) {
                Mark mark = new Mark(
                        rs.getInt("id"),
                        rs.getInt("studentid"),
                        rs.getString("mdate"),
                        rs.getInt("mark"),
                        rs.getString("type"),
                        rs.getInt("subjectid")
                );
                marks.add(mark);
            }
        } catch (SQLException e) {
            logger.error("Error reading marks", e);
        }
        return marks;
    }

    public List<Mark> readMarksByStudentId(int studentId) {
        List<Mark> marks = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM marks WHERE studentid = ?")) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Mark mark = new Mark(
                        rs.getInt("id"),
                        rs.getInt("studentid"),
                        rs.getString("mdate"),
                        rs.getInt("mark"),
                        rs.getString("type"),
                        rs.getInt("subjectid")
                );
                marks.add(mark);
            }
        } catch (SQLException e) {
            logger.error("Error reading marks by student ID", e);
        }
        return marks;
    }

    public void updateMark(int id, int markValue, String type) {
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE marks SET mark = ?, type = ? WHERE id = ?")) {

            pstmt.setInt(1, markValue);
            pstmt.setString(2, type);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();

            logger.info("Mark updated with ID: {}", id);
        } catch (SQLException e) {
            logger.error("Error updating mark", e);
            showErrorAlert("Database Error",
                    "Could not update mark",
                    e.getMessage());
        }
    }

    public void deleteMark(int id) {
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "DELETE FROM marks WHERE id = ?")) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

            logger.info("Mark deleted with ID: {}", id);
        } catch (SQLException e) {
            logger.error("Error deleting mark", e);
            showErrorAlert("Database Error",
                    "Could not delete mark",
                    e.getMessage());
        }
    }

    // Advanced Query Methods
    public int getTableRecordCount(String tableName) {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error getting record count for table: {}", tableName, e);
        }
        return 0;
    }

    public double getAverageMarkBySubject(int subjectId) {
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT AVG(mark) as avg_mark FROM marks WHERE subjectid = ?")) {

            pstmt.setInt(1, subjectId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_mark");
                }
            }
        } catch (SQLException e) {
            logger.error("Error calculating average mark", e);
        }

        return 0.0;
    }

    public Map<String, Integer> getMarkCountByType() {
        Map<String, Integer> markTypeCounts = new HashMap<>();

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT type, COUNT(*) as count FROM marks GROUP BY type")) {

            while (rs.next()) {
                markTypeCounts.put(
                        rs.getString("type"),
                        rs.getInt("count")
                );
            }
        } catch (SQLException e) {
            logger.error("Error counting mark types", e);
        }

        return markTypeCounts;
    }
}