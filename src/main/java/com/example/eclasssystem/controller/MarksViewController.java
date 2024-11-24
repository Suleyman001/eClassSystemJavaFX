package com.example.eclasssystem.controller;

import com.example.eclasssystem.services.DatabaseManager;
import com.example.eclasssystem.services.Mark;
import com.example.eclasssystem.services.Student;
import com.example.eclasssystem.services.Subject;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.util.Callback;
import java.time.LocalDate;

public class MarksViewController {
    @FXML
    private TableView<Mark> marksTable;

    @FXML
    private TableColumn<Mark, Integer> idColumn;
    @FXML
    private TableColumn<Mark, String> studentColumn;
    @FXML
    private TableColumn<Mark, String> subjectColumn;
    @FXML
    private TableColumn<Mark, Integer> markColumn;
    @FXML
    private TableColumn<Mark, String> typeColumn;
    @FXML
    private TableColumn<Mark, String> dateColumn;

    @FXML
    private ComboBox<Student> studentFilterComboBox;
    @FXML
    private ComboBox<Subject> subjectFilterComboBox;
    @FXML
    private ComboBox<String> markTypeFilterComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;

    private DatabaseManager dbManager;
    private ObservableList<Mark> masterData;

    @FXML
    public void initialize() {
        dbManager = new DatabaseManager();

        // Setup table columns
        setupTableColumns();

        // Populate filter combo boxes
        populateFilterComboBoxes();

        // Load marks
        loadMarks();

        // Setup advanced filtering
        setupAdvancedFiltering();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        markColumn.setCellValueFactory(new PropertyValueFactory<>("mark"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Custom cell factories for student and subject
        studentColumn.setCellValueFactory(cellData -> {
            Student student = dbManager.readStudentById(cellData.getValue().getStudentId());
            return new SimpleStringProperty(student != null ? student.getName() : "Unknown");
        });

        subjectColumn.setCellValueFactory(cellData -> {
            Subject subject = dbManager.readSubjectById(cellData.getValue().getSubjectId());
            return new SimpleStringProperty(subject != null ? subject.getName() : "Unknown");
        });
    }

    private void populateFilterComboBoxes() {
        // Populate student filter
        studentFilterComboBox.setItems(FXCollections.observableArrayList(
                dbManager.readAllStudents()
        ));
        studentFilterComboBox.setCellFactory(new Callback<ListView<Student>, ListCell<Student>>() {
            @Override
            public ListCell<Student> call(ListView<Student> param) {
                return new ListCell<Student>() {
                    @Override
                    protected void updateItem(Student student, boolean empty) {
                        super.updateItem(student, empty);
                        setText(empty ? "" : student.getName());
                    }
                };
            }
        });

        // Similar setup for subject filter and mark type filter
        subjectFilterComboBox.setItems(FXCollections.observableArrayList(
                dbManager.readAllSubjects()
        ));

        markTypeFilterComboBox.setItems(FXCollections.observableArrayList(
                "Oral Assessment", "Written Test", "Homework", "Project"
        ));
    }

    private void loadMarks() {
        masterData = FXCollections.observableArrayList(dbManager.readAllMarks());
        marksTable.setItems(masterData);
    }

    private void setupAdvancedFiltering() {
        FilteredList<Mark> filteredData = new FilteredList<>(masterData, p -> true);

        // Combine all filter conditions
        filteredData.setPredicate(mark -> {
            boolean studentMatch = studentFilterComboBox.getValue() == null ||
                    mark.getStudentId() == studentFilterComboBox.getValue().getId();

            boolean subjectMatch = subjectFilterComboBox.getValue() == null ||
                    mark.getSubjectId() == subjectFilterComboBox.getValue().getId();

            boolean typeMatch = markTypeFilterComboBox.getValue() == null ||
                    mark.getType().equals(markTypeFilterComboBox.getValue());

            boolean dateMatch = isDateInRange(mark.getDate());

            return studentMatch && subjectMatch && typeMatch && dateMatch;
        });

        // Listener for all filters
        studentFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> filteredData.setPredicate(filteredData.getPredicate()));
        subjectFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> filteredData.setPredicate(filteredData.getPredicate()));
        markTypeFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> filteredData.setPredicate(filteredData.getPredicate()));
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> filteredData.setPredicate(filteredData.getPredicate()));
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> filteredData.setPredicate(filteredData.getPredicate()));

        SortedList<Mark> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(marksTable.comparatorProperty());
        marksTable.setItems(sortedData);
    }

    private boolean isDateInRange(String markDate) {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (start == null && end == null) return true;

        LocalDate markLocalDate = LocalDate.parse(markDate);

        if (start != null && end == null)
            return !markLocalDate.isBefore(start);

        if (start == null && end != null)
            return !markLocalDate.isAfter(end);

        return !markLocalDate.isBefore(start) && !markLocalDate.isAfter(end);
    }

    // Add mark functionality
    @FXML
    private void handleAddMark() {
        // Similar to previous add methods, but with more complex form
        // Include student, subject, mark, type, and date selection
    }
}