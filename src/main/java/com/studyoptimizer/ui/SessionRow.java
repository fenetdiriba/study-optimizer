package com.studyoptimizer.ui;

import com.studyoptimizer.domain.StudyPlan;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SessionRow {
    private final StringProperty date = new SimpleStringProperty("");
    private final StringProperty subject = new SimpleStringProperty("");
    private final StringProperty type = new SimpleStringProperty("");
    private final IntegerProperty minutes = new SimpleIntegerProperty(0);
    private final StringProperty done = new SimpleStringProperty("");
    private final StringProperty id = new SimpleStringProperty("");

    public String getDate() {
        return date.get();
    }

    public StringProperty dateProperty() {
        return date;
    }

    public StringProperty subjectProperty() {
        return subject;
    }

    public StringProperty typeProperty() {
        return type;
    }

    public IntegerProperty minutesProperty() {
        return minutes;
    }

    public StringProperty doneProperty() {
        return done;
    }

    public StringProperty idProperty() {
        return id;
    }

    public String getId() {
        return id.get();
    }

    public static ObservableList<SessionRow> fromPlan(StudyPlan plan) {
        var list = FXCollections.<SessionRow>observableArrayList();
        if (plan == null) return list;
        for (var day : plan.getDays()) {
            for (var session : day.getSessions()) {
                var row = new SessionRow();
                row.date.set(session.getDate() == null ? "" : session.getDate().toString());
                row.subject.set(session.getSubjectName() == null ? "" : session.getSubjectName());
                row.type.set(session.getType() == null ? "" : session.getType().name());
                row.minutes.set(session.getDurationMinutes());
                row.done.set(session.isCompleted() ? "yes" : "");
                row.id.set(session.getId() == null ? "" : session.getId().toString());
                list.add(row);
            }
        }
        return list;
    }
}

