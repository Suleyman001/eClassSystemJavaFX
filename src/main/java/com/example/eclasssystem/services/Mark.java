package com.example.eclasssystem.services;

import java.util.Objects;

public class Mark {
    private int id;
    private int studentId;
    private String date;
    private int mark;
    private String type;
    private int subjectId;

    // Constructor
    public Mark(int id, int studentId, String date, int mark, String type, int subjectId) {
        this.id = id;
        this.studentId = studentId;
        this.date = date;
        this.mark = mark;
        this.type = type;
        this.subjectId = subjectId;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    // toString(), equals(), hashCode() methods


    @Override
    public String toString() {
        return "Mark{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", date='" + date + '\'' +
                ", mark=" + mark +
                ", type='" + type + '\'' +
                ", subjectId=" + subjectId +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Mark mark1 = (Mark) o;
        return id == mark1.id && studentId == mark1.studentId && mark == mark1.mark && subjectId == mark1.subjectId && Objects.equals(date, mark1.date) && Objects.equals(type, mark1.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, studentId, date, mark, type, subjectId);
    }
}
