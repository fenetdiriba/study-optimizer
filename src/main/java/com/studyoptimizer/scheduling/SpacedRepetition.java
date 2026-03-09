package com.studyoptimizer.scheduling;

import com.studyoptimizer.domain.PlanningInput;

import java.util.List;

public final class SpacedRepetition {
    private SpacedRepetition() {
    }

    public static List<Integer> intervalsDays(PlanningInput input) {
        var intervals = input.getSpacedRepetitionIntervalsDays();
        return (intervals == null || intervals.isEmpty()) ? List.of(1, 3, 7, 14) : intervals;
    }
}

