package com.example.eclasssystem.util;

import com.example.eclasssystem.exceptions.ValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

public class ValidationUtil {
    // Student Validation
    public static boolean validateStudentName(String name) {
        return name != null &&
                !name.trim().isEmpty() &&
                name.length() >= 2 &&
                name.length() <= 100 &&
                name.matches("^[A-Za-zÀ-ÿ\\s'-]+$");
    }

    public static boolean validateClassName(String className) {
        return className != null &&
                className.matches("^\\d{1,2}/[A-D]$");
    }
    public static void throwIfInvalid(boolean condition, String errorMessage)
            throws ValidationException {
        if (!condition) {
            throw new ValidationException(errorMessage);
        }
    }
    // Subject Validation
    public static boolean validateSubjectName(String name) {
        return name != null &&
                !name.trim().isEmpty() &&
                name.length() >= 2 &&
                name.length() <= 100;
    }

    public static boolean validateSubjectCategory(String category) {
        return category != null &&
                !category.trim().isEmpty() &&
                category.length() >= 2 &&
                category.length() <= 50;
    }

    // Mark Validation
    public static boolean validateMark(int mark) {
        return mark >= 1 && mark <= 5;
    }

    public static boolean validateMarkType(String type) {
        String[] validTypes = {
                "Oral Assessment",
                "Written Test",
                "Homework",
                "Project",
                "Quiz"
        };

        return type != null &&
                !type.trim().isEmpty() &&
                Arrays.stream(validTypes).anyMatch(type::equals);
    }

    // Date Validation
    public static boolean validateDate(String dateString) {
        try {
            LocalDate.parse(dateString);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

}