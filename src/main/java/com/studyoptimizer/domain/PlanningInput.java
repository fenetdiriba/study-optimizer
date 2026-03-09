package com.studyoptimizer.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlanningInput {
    private LocalDate startDate;
    private int dailyAvailableMinutes;
    private int learnBlockMinutes;
    private int reviewMinutes;
    private List<Integer> spacedRepetitionIntervalsDays = new ArrayList<>();
    private List<Subject> subjects = new ArrayList<>();

    public PlanningInput() {
    }

    public static PlanningInput defaults() {
        var input = new PlanningInput();
        input.setStartDate(LocalDate.now());
        input.setDailyAvailableMinutes(180);
        input.setLearnBlockMinutes(60);
        input.setReviewMinutes(20);
        input.setSpacedRepetitionIntervalsDays(List.of(1, 3, 7, 14));
        return input;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public int getDailyAvailableMinutes() {
        return dailyAvailableMinutes;
    }

    public void setDailyAvailableMinutes(int dailyAvailableMinutes) {
        this.dailyAvailableMinutes = dailyAvailableMinutes;
    }

    public int getLearnBlockMinutes() {
        return learnBlockMinutes;
    }

    public void setLearnBlockMinutes(int learnBlockMinutes) {
        this.learnBlockMinutes = learnBlockMinutes;
    }

    public int getReviewMinutes() {
        return reviewMinutes;
    }

    public void setReviewMinutes(int reviewMinutes) {
        this.reviewMinutes = reviewMinutes;
    }

    public List<Integer> getSpacedRepetitionIntervalsDays() {
        return spacedRepetitionIntervalsDays;
    }

    public void setSpacedRepetitionIntervalsDays(List<Integer> spacedRepetitionIntervalsDays) {
        this.spacedRepetitionIntervalsDays = spacedRepetitionIntervalsDays;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }
}

