package com.studyoptimizer.ui;

import com.studyoptimizer.domain.StudyPlan;
import com.studyoptimizer.progress.ProgressTracker;

public final class PlanFormatter {
    private PlanFormatter() {
    }

    public static String format(StudyPlan plan) {
        if (plan == null) return "";

        var sb = new StringBuilder();
        sb.append("======= STUDY PLAN =======\n\n");

        for (var day : plan.getDays()) {
            if (day.getSessions().isEmpty()) continue;
            sb.append(day.getDate()).append('\n');
            for (var s : day.getSessions()) {
                sb.append(" - ")
                        .append(s.getSubjectName())
                        .append(" [").append(s.getType()).append("] ")
                        .append(s.getDurationMinutes()).append("m")
                        .append(s.isCompleted() ? " (done)" : "")
                        .append(" id=").append(s.getId())
                        .append('\n');
            }
            sb.append('\n');
        }

        sb.append(new ProgressTracker().statusLine(plan)).append('\n');
        return sb.toString();
    }
}

