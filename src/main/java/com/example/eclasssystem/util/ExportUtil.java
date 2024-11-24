package com.example.eclasssystem.util;

import com.example.eclasssystem.services.Student;
import com.example.eclasssystem.services.Subject;
import com.itextpdf.text.pdf.PdfPCell;
import com.opencsv.CSVWriter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportUtil {
    // CSV Export for Students
    public static void exportStudentsToCSV(List<Student> students, String filePath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // Write header
            writer.writeNext(new String[]{"ID", "Name", "Class", "Gender"});

            // Write data
            for (Student student : students) {
                writer.writeNext(new String[]{
                        String.valueOf(student.getId()),
                        student.getName(),
                        student.getClassName(),
                        student.isBoy() ? "Male" : "Female"
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // CSV Export for Subjects
    public static void exportSubjectsToCSV(List<Subject> subjects, String filePath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // Write header
            writer.writeNext(new String[]{"ID", "Name", "Category"});

            // Write data
            for (Subject subject : subjects) {
                writer.writeNext(new String[]{
                        String.valueOf(subject.getId()),
                        subject.getName(),
                        subject.getCategory()
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Excel Export for Students
    public static void exportStudentsToExcel(List<Student> students, String filePath) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Students");

            // Create header row
            XSSFRow headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Class");
            headerRow.createCell(3).setCellValue("Gender");

            // Create data rows
            int rowNum = 1;
            for (Student student : students) {
                XSSFRow row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(student.getId());
                row.createCell(1).setCellValue(student.getName());
                row.createCell(2).setCellValue(student.getClassName());
                row.createCell(3).setCellValue(student.isBoy() ? "Male" : "Female");
            }

            // Write to file
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Excel Export for Subjects
    public static void exportSubjectsToExcel(List<Subject> subjects, String filePath) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Subjects");

            // Create header row
            XSSFRow headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Category");

            // Create data rows
            int rowNum = 1;
            for (Subject subject : subjects) {
                XSSFRow row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(subject.getId());
                row.createCell(1).setCellValue(subject.getName());
                row.createCell(2).setCellValue(subject.getCategory());
            }

            // Write to file
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // PDF Export
    public static void exportSubjectsToPDF(List<Subject> subjects, String filePath) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Add title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Subject List", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Create table
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Table headers
            String[] headers = {"ID", "Name", "Category"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);
            }

            // Add subject data
            for (Subject subject : subjects) {
                table.addCell(String.valueOf(subject.getId()));
                table.addCell(subject.getName());
                table.addCell(subject.getCategory());
            }

            document.add(table);
            document.close();
            System.out.println("PDF export successful: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

