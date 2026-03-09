package com.studyoptimizer.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudyPlan {
    private LocalDate startDate;
    private LocalDate endDate;
    private int dailyAvailableMinutes;
    private Instant generatedAt;
    private List<StudyDay> days = new ArrayList<>();

    public StudyPlan() {
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getDailyAvailableMinutes() {
        return dailyAvailableMinutes;
    }

    public void setDailyAvailableMinutes(int dailyAvailableMinutes) {
        this.dailyAvailableMinutes = dailyAvailableMinutes;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }

    public List<StudyDay> getDays() {
        return days;
    }

    public void setDays(List<StudyDay> days) {
        this.days = days;
    }
}

