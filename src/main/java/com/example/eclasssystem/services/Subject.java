package com.example.eclasssystem.services;

import java.util.Objects;

public class Subject {
    private int id;
    private String name;
    private String category;

    // Constructor
    public Subject(int id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // toString(), equals(), hashCode() methods

    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return id == subject.id && Objects.equals(name, subject.name) && Objects.equals(category, subject.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, category);
    }
}
