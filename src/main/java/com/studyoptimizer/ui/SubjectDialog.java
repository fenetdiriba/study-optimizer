package com.studyoptimizer.ui;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;

public class SubjectDialog extends Dialog<SubjectRow> {
    public SubjectDialog(String title, SubjectRow existing) {
        setTitle(title);
        setHeaderText(null);

        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        var nameField = new TextField(existing == null ? "" : existing.getName());
        var examField = new TextField(existing == null ? LocalDate.now().plusDays(14).toString() : existing.getExamDate());

        var difficultySpinner = new Spinner<Integer>();
        difficultySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, existing == null ? 3 : existing.getDifficulty()));
        difficultySpinner.setEditable(true);

        var hoursSpinner = new Spinner<Integer>();
        hoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 500, existing == null ? 10 : existing.getEstimatedHours()));
        hoursSpinner.setEditable(true);

        var grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(12));

        grid.addRow(0, new Label("Name"), nameField);
        grid.addRow(1, new Label("Exam date (YYYY-MM-DD)"), examField);
        grid.addRow(2, new Label("Difficulty (1-5)"), difficultySpinner);
        grid.addRow(3, new Label("Estimated hours"), hoursSpinner);

        getDialogPane().setContent(grid);

        setResultConverter(btn -> {
            if (btn != ButtonType.OK) return null;
            var row = new SubjectRow();
            if (existing != null) row.setId(existing.getId());
            row.setName(nameField.getText());
            row.setExamDate(examField.getText());
            row.setDifficulty(difficultySpinner.getValue());
            row.setEstimatedHours(hoursSpinner.getValue());

            // Validate by converting to domain model.
            row.toDomain();
            return row;
        });
    }
}

