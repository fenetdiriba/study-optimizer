package com.studyoptimizer.ui;

import com.studyoptimizer.domain.Subject;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.util.UUID;

public class SubjectRow {
    private UUID id;
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty examDate = new SimpleStringProperty("");
    private final IntegerProperty difficulty = new SimpleIntegerProperty(3);
    private final IntegerProperty estimatedHours = new SimpleIntegerProperty(10);

    public SubjectRow() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getExamDate() {
        return examDate.get();
    }

    public void setExamDate(String examDate) {
        this.examDate.set(examDate);
    }

    public StringProperty examDateProperty() {
        return examDate;
    }

    public int getDifficulty() {
        return difficulty.get();
    }

    public void setDifficulty(int difficulty) {
        this.difficulty.set(difficulty);
    }

    public IntegerProperty difficultyProperty() {
        return difficulty;
    }

    public int getEstimatedHours() {
        return estimatedHours.get();
    }

    public void setEstimatedHours(int estimatedHours) {
        this.estimatedHours.set(estimatedHours);
    }

    public IntegerProperty estimatedHoursProperty() {
        return estimatedHours;
    }

    public Subject toDomain() {
        if (getName() == null || getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Subject name is required.");
        }
        LocalDate date;
        try {
            date = LocalDate.parse(getExamDate().trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid exam date for " + getName() + ". Use YYYY-MM-DD.");
        }

        var s = new Subject();
        s.setId(getId() == null ? UUID.randomUUID() : getId());
        s.setName(getName().trim());
        s.setExamDate(date);
        s.setDifficulty(Math.max(1, Math.min(5, getDifficulty())));
        s.setEstimatedMinutes(Math.max(0, getEstimatedHours()) * 60);
        return s;
    }

    public static SubjectRow fromDomain(Subject subject) {
        var row = new SubjectRow();
        row.setId(subject.getId());
        row.setName(subject.getName());
        row.setExamDate(subject.getExamDate() == null ? "" : subject.getExamDate().toString());
        row.setDifficulty(subject.getDifficulty());
        row.setEstimatedHours(subject.getEstimatedMinutes() / 60);
        return row;
    }
}

