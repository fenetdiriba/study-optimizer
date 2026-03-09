package com.studyoptimizer.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudyDay {
    private LocalDate date;
    private List<StudySession> sessions = new ArrayList<>();

    public StudyDay() {
    }

    public StudyDay(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<StudySession> getSessions() {
        return sessions;
    }

    public void setSessions(List<StudySession> sessions) {
        this.sessions = sessions;
    }
}

