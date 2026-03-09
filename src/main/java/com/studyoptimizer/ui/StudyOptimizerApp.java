package com.studyoptimizer.ui;

import com.studyoptimizer.domain.PlanningInput;
import com.studyoptimizer.domain.StudyPlan;
import com.studyoptimizer.progress.ProgressTracker;
import com.studyoptimizer.scheduling.ScheduleGenerator;
import com.studyoptimizer.storage.FileStore;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.UUID;

public class StudyOptimizerApp extends Application {
    private final FileStore store = new FileStore();
    private final ScheduleGenerator generator = new ScheduleGenerator();
    private final ProgressTracker progressTracker = new ProgressTracker();

    private final ObservableList<SubjectRow> subjects = FXCollections.observableArrayList();
    private StudyPlan currentPlan;

    private final Spinner<Integer> dailyHoursSpinner = new Spinner<>();
    private final Spinner<Integer> learnBlockSpinner = new Spinner<>();
    private final Spinner<Integer> reviewMinutesSpinner = new Spinner<>();

    private final TextArea planText = new TextArea();
    private final TableView<SubjectRow> subjectsTable = new TableView<>();
    private final TableView<SessionRow> sessionsTable = new TableView<>();
    private final Label statusLabel = new Label("Progress: 0/0 (0%)");

    public static void launchApp(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Study Optimizer (MVP)");

        configureSpinners();

        buildSubjectsTable();
        var leftPane = new VBox(8, new Label("Subjects"), subjectsTable, buildSubjectButtons());
        leftPane.setPadding(new Insets(10));
        VBox.setVgrow(subjectsTable, Priority.ALWAYS);

        var rightPane = new VBox(8,
                new Label("Generated plan"),
                buildPlanArea(),
                new Label("Sessions"),
                buildSessionsTable(),
                statusLabel
        );
        rightPane.setPadding(new Insets(10));
        VBox.setVgrow(planText, Priority.ALWAYS);
        VBox.setVgrow(sessionsTable, Priority.ALWAYS);

        var root = new BorderPane();
        root.setTop(buildTopBar());
        root.setLeft(leftPane);
        root.setCenter(rightPane);

        stage.setScene(new Scene(root, 1100, 700));
        stage.show();

        tryLoadInputOnStartup();
        tryLoadPlanOnStartup();
    }

    private void configureSpinners() {
        dailyHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 24, 3));
        learnBlockSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(15, 240, 60, 15));
        reviewMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 120, 20, 5));
        dailyHoursSpinner.setEditable(true);
        learnBlockSpinner.setEditable(true);
        reviewMinutesSpinner.setEditable(true);
    }

    private ToolBar buildTopBar() {
        var loadInput = new Button("Load input");
        loadInput.setOnAction(e -> loadInput());

        var saveInput = new Button("Save input");
        saveInput.setOnAction(e -> saveInput());

        var generate = new Button("Generate plan");
        generate.setDefaultButton(true);
        generate.setOnAction(e -> generatePlan());

        var loadPlan = new Button("Load plan");
        loadPlan.setOnAction(e -> loadPlan());

        var markDone = new Button("Mark session done");
        markDone.setOnAction(e -> markSelectedSessionDone());

        var bar = new ToolBar(
                loadInput, saveInput, new Separator(),
                new Label("Daily hours:"), dailyHoursSpinner,
                new Label("Learn block (min):"), learnBlockSpinner,
                new Label("Review (min):"), reviewMinutesSpinner,
                new Separator(),
                generate, loadPlan, markDone
        );
        bar.setPadding(new Insets(6));
        return bar;
    }

    private void buildSubjectsTable() {
        subjectsTable.setItems(subjects);
        subjectsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        var nameCol = new TableColumn<SubjectRow, String>("Name");
        nameCol.setCellValueFactory(c -> c.getValue().nameProperty());
        nameCol.setPrefWidth(220);

        var examCol = new TableColumn<SubjectRow, String>("Exam date");
        examCol.setCellValueFactory(c -> c.getValue().examDateProperty());
        examCol.setPrefWidth(120);

        var diffCol = new TableColumn<SubjectRow, Number>("Difficulty");
        diffCol.setCellValueFactory(c -> c.getValue().difficultyProperty());
        diffCol.setPrefWidth(90);

        var hoursCol = new TableColumn<SubjectRow, Number>("Estimated hours");
        hoursCol.setCellValueFactory(c -> c.getValue().estimatedHoursProperty());
        hoursCol.setPrefWidth(130);

        subjectsTable.getColumns().setAll(nameCol, examCol, diffCol, hoursCol);
    }

    private HBox buildSubjectButtons() {
        var add = new Button("Add…");
        add.setOnAction(e -> addSubject());

        var edit = new Button("Edit…");
        edit.setOnAction(e -> editSelectedSubject());

        var remove = new Button("Remove");
        remove.setOnAction(e -> removeSelectedSubject());

        var box = new HBox(8, add, edit, remove);
        return box;
    }

    private TextArea buildPlanArea() {
        planText.setEditable(false);
        planText.setWrapText(false);
        planText.setPromptText("Generate a plan to see output here.");
        return planText;
    }

    private TableView<SessionRow> buildSessionsTable() {
        sessionsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        var dateCol = new TableColumn<SessionRow, String>("Date");
        dateCol.setCellValueFactory(c -> c.getValue().dateProperty());
        dateCol.setPrefWidth(110);

        var subjectCol = new TableColumn<SessionRow, String>("Subject");
        subjectCol.setCellValueFactory(c -> c.getValue().subjectProperty());
        subjectCol.setPrefWidth(220);

        var typeCol = new TableColumn<SessionRow, String>("Type");
        typeCol.setCellValueFactory(c -> c.getValue().typeProperty());
        typeCol.setPrefWidth(80);

        var minutesCol = new TableColumn<SessionRow, Number>("Minutes");
        minutesCol.setCellValueFactory(c -> c.getValue().minutesProperty());
        minutesCol.setPrefWidth(80);

        var doneCol = new TableColumn<SessionRow, String>("Done");
        doneCol.setCellValueFactory(c -> c.getValue().doneProperty());
        doneCol.setPrefWidth(70);

        var idCol = new TableColumn<SessionRow, String>("Session ID");
        idCol.setCellValueFactory(c -> c.getValue().idProperty());
        idCol.setPrefWidth(300);

        sessionsTable.getColumns().addAll(dateCol, subjectCol, typeCol, minutesCol, doneCol, idCol);
        return sessionsTable;
    }

    private void addSubject() {
        var dialog = new SubjectDialog("Add subject", null);
        var result = dialog.showAndWait();
        result.ifPresent(subjects::add);
    }

    private void editSelectedSubject() {
        var selected = getSelectedSubject();
        if (selected == null) return;

        var dialog = new SubjectDialog("Edit subject", selected);
        var result = dialog.showAndWait();
        result.ifPresent(updated -> {
            selected.setName(updated.getName());
            selected.setExamDate(updated.getExamDate());
            selected.setDifficulty(updated.getDifficulty());
            selected.setEstimatedHours(updated.getEstimatedHours());
        });
    }

    private void removeSelectedSubject() {
        var selected = getSelectedSubject();
        if (selected == null) return;

        var confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Remove subject \"" + selected.getName() + "\"?",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText(null);
        var res = confirm.showAndWait().orElse(ButtonType.CANCEL);
        if (res == ButtonType.OK) {
            subjects.remove(selected);
        }
    }

    private SubjectRow getSelectedSubject() {
        return subjectsTable.getSelectionModel().getSelectedItem();
    }

    private void loadInput() {
        try {
            var input = store.loadInput();
            dailyHoursSpinner.getValueFactory().setValue(input.getDailyAvailableMinutes() / 60);
            learnBlockSpinner.getValueFactory().setValue(input.getLearnBlockMinutes());
            reviewMinutesSpinner.getValueFactory().setValue(input.getReviewMinutes());

            subjects.clear();
            for (var s : input.getSubjects()) {
                subjects.add(SubjectRow.fromDomain(s));
            }
        } catch (Exception ex) {
            showError("Load input failed", ex.getMessage());
        }
    }

    private void saveInput() {
        try {
            var input = buildInputFromUI();
            store.saveInput(input);
        } catch (Exception ex) {
            showError("Save input failed", ex.getMessage());
        }
    }

    private void generatePlan() {
        try {
            var input = buildInputFromUI();
            store.saveInput(input);
            currentPlan = generator.generate(input);
            store.savePlan(currentPlan);
            refreshPlanViews();
        } catch (Exception ex) {
            showError("Generate failed", ex.getMessage());
        }
    }

    private void loadPlan() {
        try {
            currentPlan = store.loadPlan();
            refreshPlanViews();
        } catch (Exception ex) {
            showError("Load plan failed", ex.getMessage());
        }
    }

    private void markSelectedSessionDone() {
        if (currentPlan == null) return;
        var selected = sessionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            var id = UUID.fromString(selected.getId());
            var changed = progressTracker.markCompleted(currentPlan, id);
            if (changed) {
                store.savePlan(currentPlan);
                refreshPlanViews();
            }
        } catch (Exception ex) {
            showError("Mark done failed", ex.getMessage());
        }
    }

    private PlanningInput buildInputFromUI() {
        var input = PlanningInput.defaults();
        input.setDailyAvailableMinutes(Math.max(0, dailyHoursSpinner.getValue()) * 60);
        input.setLearnBlockMinutes(learnBlockSpinner.getValue());
        input.setReviewMinutes(reviewMinutesSpinner.getValue());

        var domainSubjects = new ArrayList<com.studyoptimizer.domain.Subject>();
        for (var row : subjects) {
            domainSubjects.add(row.toDomain());
        }
        input.setSubjects(domainSubjects);
        return input;
    }

    private void refreshPlanViews() {
        if (currentPlan == null) {
            planText.clear();
            sessionsTable.setItems(FXCollections.observableArrayList());
            statusLabel.setText("Progress: 0/0 (0%)");
            return;
        }

        planText.setText(PlanFormatter.format(currentPlan));
        sessionsTable.setItems(SessionRow.fromPlan(currentPlan));
        statusLabel.setText(progressTracker.statusLine(currentPlan));
    }

    private void tryLoadInputOnStartup() {
        try {
            if (java.nio.file.Files.exists(store.inputPath())) {
                loadInput();
            }
        } catch (Exception ignored) {
        }
    }

    private void tryLoadPlanOnStartup() {
        try {
            if (java.nio.file.Files.exists(store.planPath())) {
                currentPlan = store.loadPlan();
                refreshPlanViews();
            }
        } catch (Exception ignored) {
        }
    }

    private void showError(String title, String message) {
        var alert = new Alert(Alert.AlertType.ERROR, message == null ? "(no details)" : message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}

