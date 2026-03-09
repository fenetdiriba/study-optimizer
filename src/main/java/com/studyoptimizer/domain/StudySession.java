package com.studyoptimizer.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class StudySession {
    private UUID id;
    private UUID subjectId;
    private String subjectName;
    private LocalDate date;
    private int durationMinutes;
    private SessionType type;
    private int repetitionIndex;
    private boolean completed;
    private Instant completedAt;

    public StudySession() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(UUID subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public SessionType getType() {
        return type;
    }

    public void setType(SessionType type) {
        this.type = type;
    }

    public int getRepetitionIndex() {
        return repetitionIndex;
    }

    public void setRepetitionIndex(int repetitionIndex) {
        this.repetitionIndex = repetitionIndex;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }
}

