package com.studyoptimizer;

import com.studyoptimizer.domain.PlanningInput;
import com.studyoptimizer.domain.SessionType;
import com.studyoptimizer.domain.Subject;
import com.studyoptimizer.scheduling.ScheduleGenerator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScheduleGeneratorTest {
    @Test
    void doesNotScheduleAfterExamDate() {
        var subject = new Subject();
        subject.setId(UUID.randomUUID());
        subject.setName("Math");
        subject.setDifficulty(5);
        subject.setExamDate(LocalDate.now().plusDays(3));
        subject.setEstimatedMinutes(10 * 60);

        var input = PlanningInput.defaults();
        input.setStartDate(LocalDate.now());
        input.setDailyAvailableMinutes(180);
        input.getSubjects().add(subject);

        var plan = new ScheduleGenerator().generate(input);

        plan.getDays().forEach(day -> day.getSessions().forEach(session -> {
            assertTrue(!session.getDate().isAfter(subject.getExamDate()));
            assertTrue(session.getType() == SessionType.LEARN || session.getType() == SessionType.REVIEW);
        }));
    }

    @Test
    void dailyBudgetIsRespected() {
        var a = new Subject();
        a.setId(UUID.randomUUID());
        a.setName("A");
        a.setDifficulty(5);
        a.setExamDate(LocalDate.now().plusDays(10));
        a.setEstimatedMinutes(50 * 60);

        var b = new Subject();
        b.setId(UUID.randomUUID());
        b.setName("B");
        b.setDifficulty(1);
        b.setExamDate(LocalDate.now().plusDays(10));
        b.setEstimatedMinutes(50 * 60);

        var input = PlanningInput.defaults();
        input.setStartDate(LocalDate.now());
        input.setDailyAvailableMinutes(120);
        input.setLearnBlockMinutes(60);
        input.setReviewMinutes(20);
        input.getSubjects().add(a);
        input.getSubjects().add(b);

        var plan = new ScheduleGenerator().generate(input);
        plan.getDays().forEach(day -> {
            int total = day.getSessions().stream().mapToInt(s -> s.getDurationMinutes()).sum();
            assertTrue(total <= input.getDailyAvailableMinutes());
        });
    }
}

