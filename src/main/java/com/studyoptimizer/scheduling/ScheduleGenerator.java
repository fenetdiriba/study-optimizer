package com.studyoptimizer.scheduling;

import com.studyoptimizer.domain.PlanningInput;
import com.studyoptimizer.domain.SessionType;
import com.studyoptimizer.domain.StudyDay;
import com.studyoptimizer.domain.StudyPlan;
import com.studyoptimizer.domain.StudySession;
import com.studyoptimizer.domain.Subject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScheduleGenerator {
    public StudyPlan generate(PlanningInput input) {
        if (input.getSubjects() == null || input.getSubjects().isEmpty()) {
            throw new IllegalArgumentException("No subjects provided.");
        }

        var start = (input.getStartDate() == null) ? LocalDate.now() : input.getStartDate();
        var end = input.getSubjects().stream()
                .map(Subject::getExamDate)
                .max(LocalDate::compareTo)
                .orElseThrow();

        var plan = new StudyPlan();
        plan.setStartDate(start);
        plan.setEndDate(end);
        plan.setDailyAvailableMinutes(Math.max(0, input.getDailyAvailableMinutes()));
        plan.setGeneratedAt(Instant.now());

        var remainingLearnMinutes = new HashMap<UUID, Integer>();
        for (var s : input.getSubjects()) {
            remainingLearnMinutes.put(s.getId(), Math.max(0, s.getEstimatedMinutes()));
        }

        var reviewsByDate = new HashMap<LocalDate, Deque<StudySession>>();
        var intervals = SpacedRepetition.intervalsDays(input);

        for (var date = start; !date.isAfter(end); date = date.plusDays(1)) {
            int remainingBudget = plan.getDailyAvailableMinutes();
            var day = new StudyDay(date);

            remainingBudget = scheduleDueReviews(date, remainingBudget, reviewsByDate, day);

            int learnBlock = Math.max(15, input.getLearnBlockMinutes());
            while (remainingBudget >= 15) {
                var next = pickNextSubject(input.getSubjects(), date, remainingLearnMinutes);
                if (next == null) break;

                int remainingForSubject = remainingLearnMinutes.getOrDefault(next.getId(), 0);
                if (remainingForSubject <= 0) break;

                int duration = Math.min(learnBlock, Math.min(remainingForSubject, remainingBudget));
                if (duration < 15) break;

                var learn = newSession(next, date, duration, SessionType.LEARN, 0);
                day.getSessions().add(learn);
                remainingBudget -= duration;
                remainingLearnMinutes.put(next.getId(), remainingForSubject - duration);

                scheduleReviews(next, learn, intervals, input.getReviewMinutes(), reviewsByDate);
            }

            day.getSessions().sort(Comparator
                    .comparing(StudySession::getType)
                    .thenComparing(StudySession::getSubjectName, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(StudySession::getDurationMinutes).reversed());

            plan.getDays().add(day);
        }

        return plan;
    }

    private static int scheduleDueReviews(
            LocalDate date,
            int remainingBudget,
            Map<LocalDate, Deque<StudySession>> reviewsByDate,
            StudyDay day
    ) {
        var due = reviewsByDate.get(date);
        if (due == null || due.isEmpty() || remainingBudget <= 0) return remainingBudget;

        while (!due.isEmpty()) {
            var session = due.peekFirst();
            if (session.getDurationMinutes() <= remainingBudget) {
                day.getSessions().add(due.removeFirst());
                remainingBudget -= session.getDurationMinutes();
                continue;
            }
            var tomorrow = date.plusDays(1);
            reviewsByDate.computeIfAbsent(tomorrow, __ -> new ArrayDeque<>()).addLast(due.removeFirst());
        }

        return remainingBudget;
    }

    private static Subject pickNextSubject(List<Subject> subjects, LocalDate date, Map<UUID, Integer> remaining) {
        Subject best = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (var s : subjects) {
            if (s.getExamDate() == null || s.getId() == null) continue;
            if (date.isAfter(s.getExamDate())) continue;
            if (remaining.getOrDefault(s.getId(), 0) <= 0) continue;

            long daysUntil = Math.max(0, ChronoUnit.DAYS.between(date, s.getExamDate()));
            double score = (double) Math.max(1, s.getDifficulty()) / (double) (daysUntil + 1);
            if (score > bestScore) {
                bestScore = score;
                best = s;
            } else if (score == bestScore && best != null) {
                var nameA = (s.getName() == null) ? "" : s.getName();
                var nameB = (best.getName() == null) ? "" : best.getName();
                if (nameA.compareToIgnoreCase(nameB) < 0) best = s;
            }
        }
        return best;
    }

    private static void scheduleReviews(
            Subject subject,
            StudySession learn,
            List<Integer> intervals,
            int reviewMinutes,
            Map<LocalDate, Deque<StudySession>> reviewsByDate
    ) {
        int minutes = Math.max(5, reviewMinutes);
        int idx = 1;
        for (var interval : intervals) {
            if (interval == null || interval <= 0) continue;
            var reviewDate = learn.getDate().plusDays(interval);
            if (reviewDate.isAfter(subject.getExamDate())) continue;

            var review = newSession(subject, reviewDate, minutes, SessionType.REVIEW, idx);
            reviewsByDate.computeIfAbsent(reviewDate, __ -> new ArrayDeque<>()).addLast(review);
            idx++;
        }
    }

    private static StudySession newSession(Subject subject, LocalDate date, int minutes, SessionType type, int repetitionIndex) {
        var s = new StudySession();
        s.setId(UUID.randomUUID());
        s.setSubjectId(subject.getId());
        s.setSubjectName(subject.getName());
        s.setDate(date);
        s.setDurationMinutes(minutes);
        s.setType(type);
        s.setRepetitionIndex(repetitionIndex);
        s.setCompleted(false);
        s.setCompletedAt(null);
        return s;
    }
}

