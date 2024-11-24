package com.example.eclasssystem.tests;

import com.example.eclasssystem.services.DatabaseManager;
import com.example.eclasssystem.util.ValidationUtil;

public class ManualTestRunner {
    public static void main(String[] args) {
        testDatabaseConnection();
        testValidationUtils();
    }

    private static void testDatabaseConnection() {
        System.out.println("Testing Database Connection...");
        DatabaseManager dbManager = new DatabaseManager();
        boolean connected = dbManager.testConnection();
        System.out.println("Database Connection: " + (connected ? "✓ Successful" : "✗ Failed"));
    }

    private static void testValidationUtils() {
        System.out.println("\nTesting Validation Utilities...");

        // Student Name Validation
        testStudentName("John Doe", true);
        testStudentName("", false);
        testStudentName("A", false);

        // Class Name Validation
        testClassName("12/A", true);
        testClassName("9/B", true);
        testClassName("13/E", false);
    }

    private static void testStudentName(String name, boolean expected) {
        boolean result = ValidationUtil.validateStudentName(name);
        System.out.printf("Student Name '%s': %s%n",
                name,
                (result == expected ? "✓ Passed" : "✗ Failed")
        );
    }

    private static void testClassName(String className, boolean expected) {
        boolean result = ValidationUtil.validateClassName(className);
        System.out.printf("Class Name '%s': %s%n",
                className,
                (result == expected ? "✓ Passed" : "✗ Failed")
        );
    }
}