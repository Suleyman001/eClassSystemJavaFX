package com.example.eclasssystem.controller;

import com.example.eclasssystem.services.DatabaseManager;
import com.example.eclasssystem.services.Student;
import com.example.eclasssystem.util.ValidationUtil;
import com.example.eclasssystem.exceptions.ValidationException;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.layout.GridPane;

import java.util.Optional;
import java.util.stream.Collectors;

public class StudentsViewController {
    @FXML
    private TableView<Student> studentsTable;

    @FXML
    private TableColumn<Student, Integer> idColumn;
    @FXML
    private TableColumn<Student, String> nameColumn;
    @FXML
    private TableColumn<Student, String> classColumn;
    @FXML
    private TableColumn<Student, Boolean> genderColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> classFilterComboBox;

    private DatabaseManager dbManager;
    private ObservableList<Student> masterData;

    @FXML
    public void initialize() {
        dbManager = new DatabaseManager();

        // Setup table columns
        setupTableColumns();

        // Load students
        loadStudents();

        // Setup search and filter
        setupSearchAndFilter();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        classColumn.setCellValueFactory(new PropertyValueFactory<>("className"));

        // Custom cell factory for gender
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("boy"));
        genderColumn.setCellFactory(column -> new TableCell<Student, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : (item ? "Male" : "Female"));
            }
        });
    }

    private void loadStudents() {
        masterData = FXCollections.observableArrayList(dbManager.readAllStudents());
        studentsTable.setItems(masterData);

        // Populate class filter
        classFilterComboBox.setItems(FXCollections.observableArrayList(
                masterData.stream()
                        .map(Student::getClassName)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList())
        ));
    }

    private void setupSearchAndFilter() {
        // Wrap the ObservableList in a FilteredList
        FilteredList<Student> filteredData = new FilteredList<>(masterData, p -> true);

        // Set the filter Predicate whenever the filter changes
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(student -> {
                // If filter text is empty, display all students
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare student name and class with filter text
                String lowerCaseFilter = newValue.toLowerCase();
                return student.getName().toLowerCase().contains(lowerCaseFilter) ||
                        student.getClassName().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // Bind class filter
        classFilterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(student -> {
                // If no class selected, show all students
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare student class with selected class
                return student.getClassName().equals(newValue);
            });
        });

        // Wrap the FilteredList in a SortedList
        SortedList<Student> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(studentsTable.comparatorProperty());

        // Add sorted (and filtered) data to the table
        studentsTable.setItems(sortedData);
    }

    @FXML
    private void handleAddStudent() {
        // Open a dialog to add a new student
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Add New Student");

        // Create dialog content (form with fields)
        GridPane grid = new GridPane();
        TextField nameField = new TextField();
        TextField classField = new TextField();
        CheckBox genderCheckBox = new CheckBox("Male");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Class:"), 0, 1);
        grid.add(classField, 1, 1);
        grid.add(new Label("Gender:"), 0, 2);
        grid.add(genderCheckBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Add buttons
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        // Disable save button initially and add validation
        dialog.getDialogPane().lookupButton(saveButton).addEventFilter(
                javafx.event.ActionEvent.ACTION,
                event -> {
                    try {
                        // Validate inputs
                        ValidationUtil.throwIfInvalid(
                                ValidationUtil.validateStudentName(nameField.getText()),
                                "Invalid student name"
                        );

                        ValidationUtil.throwIfInvalid(
                                ValidationUtil.validateClassName(classField.getText()),
                                "Invalid class name"
                        );

                        // If validation passes, allow save
                    } catch (ValidationException e) {
                        // Show error and prevent dialog from closing
                        event.consume();
                        showValidationErrorAlert(e.getMessage());
                    }
                }
        );

        // Convert result to student when save is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                return new Student(0,
                        nameField.getText(),
                        classField.getText(),
                        genderCheckBox.isSelected()
                );
            }
            return null;
        });

        // Show dialog and process result
        Optional<Student> result = dialog.showAndWait();
        result.ifPresent(student -> {
            try {
                // Validate again before saving
                ValidationUtil.throwIfInvalid(
                        ValidationUtil.validateStudentName(student.getName()),
                        "Invalid student name"
                );

                ValidationUtil.throwIfInvalid(
                        ValidationUtil.validateClassName(student.getClassName()),
                        "Invalid class name"
                );

                // Proceed with student creation
                dbManager.createStudent(
                        student.getName(),
                        student.getClassName(),
                        student.isBoy()
                );

                loadStudents(); // Refresh the table
            } catch (ValidationException e) {
                showValidationErrorAlert(e.getMessage());
            }
        });
    }

    // Validation Error Alert Method
    private void showValidationErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Input Validation Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleDeleteStudent() {
        Student selectedStudent = studentsTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Delete Student");
            confirmDialog.setHeaderText("Are you sure you want to delete this student?");
            confirmDialog.setContentText(selectedStudent.getName());

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                dbManager.deleteStudent(selectedStudent.getId());
                loadStudents(); // Refresh the table
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Student Selected");
            alert.setContentText("Please select a student to delete.");
            alert.showAndWait();
        }
    }
}