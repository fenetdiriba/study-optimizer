package com.studyoptimizer.progress;

import com.studyoptimizer.domain.StudyPlan;

import java.time.Instant;
import java.util.UUID;

public class ProgressTracker {
    public boolean markCompleted(StudyPlan plan, UUID sessionId) {
        if (plan == null || sessionId == null) return false;
        for (var day : plan.getDays()) {
            for (var session : day.getSessions()) {
                if (sessionId.equals(session.getId())) {
                    if (!session.isCompleted()) {
                        session.setCompleted(true);
                        session.setCompletedAt(Instant.now());
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public ProgressSummary summarize(StudyPlan plan) {
        int total = 0;
        int completed = 0;
        if (plan != null) {
            for (var day : plan.getDays()) {
                total += day.getSessions().size();
                for (var s : day.getSessions()) {
                    if (s.isCompleted()) completed++;
                }
            }
        }
        return new ProgressSummary(total, completed);
    }

    public String statusLine(StudyPlan plan) {
        var summary = summarize(plan);
        if (summary.totalSessions() == 0) return "Progress: 0/0 (0%)";
        int pct = (int) Math.round(100.0 * summary.completedSessions() / summary.totalSessions());
        return "Progress: " + summary.completedSessions() + "/" + summary.totalSessions() + " (" + pct + "%)";
    }

    public record ProgressSummary(int totalSessions, int completedSessions) {
    }
}

