package com.example.eclasssystem.services;

import java.util.Objects;

// Model Classes
public class Student {
    private int id;
    private String name;
    private String className;
    private boolean isBoy;

    // Constructor
    public Student(int id, String name, String className, boolean isBoy) {
        this.id = id;
        this.name = name;
        this.className = className;
        this.isBoy = isBoy;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isBoy() {
        return isBoy;
    }

    public void setBoy(boolean boy) {
        isBoy = boy;
    }

    // toString(), equals(), hashCode() methods

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id == student.id && isBoy == student.isBoy && Objects.equals(name, student.name) && Objects.equals(className, student.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, className, isBoy);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", className='" + className + '\'' +
                ", isBoy=" + isBoy +
                '}';
    }
}
