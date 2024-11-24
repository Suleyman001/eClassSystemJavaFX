package com.example.eclasssystem.controller;

import com.example.eclasssystem.services.DatabaseManager;
import com.example.eclasssystem.services.Subject;
import com.example.eclasssystem.util.ExportUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert;
import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class SubjectsViewController {
    @FXML
    private TableView<Subject> subjectsTable;

    @FXML
    private TableColumn<Subject, Integer> idColumn;
    @FXML
    private TableColumn<Subject, String> nameColumn;
    @FXML
    private TableColumn<Subject, String> categoryColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> categoryFilterComboBox;

    private DatabaseManager dbManager;
    private ObservableList<Subject> masterData;

    @FXML
    public void initialize() {
        dbManager = new DatabaseManager();

        // Setup table columns
        setupTableColumns();

        // Load subjects
        loadSubjects();

        // Setup search and filter
        setupSearchAndFilter();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
    }

    private void loadSubjects() {
        masterData = FXCollections.observableArrayList(dbManager.readAllSubjects());
        subjectsTable.setItems(masterData);

        // Populate category filter
        categoryFilterComboBox.setItems(FXCollections.observableArrayList(
                masterData.stream()
                        .map(Subject::getCategory)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList())
        ));
    }

    private void setupSearchAndFilter() {
        // Filtering logic similar to StudentsViewController
        FilteredList<Subject> filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(subject -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                return subject.getName().toLowerCase().contains(lowerCaseFilter) ||
                        subject.getCategory().toLowerCase().contains(lowerCaseFilter);
            });
        });

        categoryFilterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(subject -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                return subject.getCategory().equals(newValue);
            });
        });

        SortedList<Subject> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(subjectsTable.comparatorProperty());
        subjectsTable.setItems(sortedData);
    }

    @FXML
    private void handleAddSubject() {
        Dialog<Subject> dialog = new Dialog<>();
        dialog.setTitle("Add New Subject");

        GridPane grid = new GridPane();
        TextField nameField = new TextField();
        TextField categoryField = new TextField();

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(categoryField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                return new Subject(0,
                        nameField.getText(),
                        categoryField.getText()
                );
            }
            return null;
        });

        Optional<Subject> result = dialog.showAndWait();
        result.ifPresent(subject -> {
            dbManager.createSubject(
                    subject.getName(),
                    subject.getCategory()
            );
            loadSubjects();
        });
    }

    @FXML
    private void handleDeleteSubject() {
        Subject selectedSubject = subjectsTable.getSelectionModel().getSelectedItem();
        if (selectedSubject != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Delete Subject");
            confirmDialog.setHeaderText("Are you sure you want to delete this subject?");
            confirmDialog.setContentText(selectedSubject.getName());

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                dbManager.deleteSubject(selectedSubject.getId());
                loadSubjects();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Subject Selected");
            alert.setContentText("Please select a subject to delete.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleExportToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Subjects to CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File file = fileChooser.showSaveDialog(subjectsTable.getScene().getWindow());

        if (file != null) {
            // Create a list of subjects from ObservableList
            ArrayList<Subject> subjectList = new ArrayList<>(masterData);
            ExportUtil.exportSubjectsToCSV(subjectList, file.getAbsolutePath());
            showSuccessAlert("CSV Export", "Subjects exported successfully to " + file.getName());
        }
    }

    @FXML
    private void handleExportToExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Subjects to Excel");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        File file = fileChooser.showSaveDialog(subjectsTable.getScene().getWindow());

        if (file != null) {
            // Create a list of subjects from ObservableList
            ArrayList<Subject> subjectList = new ArrayList<>(masterData);
            ExportUtil.exportSubjectsToExcel(subjectList, file.getAbsolutePath());
            showSuccessAlert("Excel Export", "Subjects exported successfully to " + file.getName());
        }
    }

    @FXML
    private void handleExportToPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Subjects to PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        File file = fileChooser.showSaveDialog(subjectsTable.getScene().getWindow());

        if (file != null) {
            // Create a list of subjects from ObservableList
            ArrayList<Subject> subjectList = new ArrayList<>(masterData);
            ExportUtil.exportSubjectsToPDF(subjectList, file.getAbsolutePath());
            showSuccessAlert("PDF Export", "Subjects exported successfully to " + file.getName());
        }
    }

    // Add this method to the class
    private void showSuccessAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}