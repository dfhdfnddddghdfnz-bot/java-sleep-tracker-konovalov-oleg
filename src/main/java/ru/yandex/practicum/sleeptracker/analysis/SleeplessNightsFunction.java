package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SleeplessNightsFunction implements SleepAnalysisFunction<Long> {

    public static final String DESCRIPTION = "Количество бессонных ночей";

    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        Set<LocalDate> nightsWithSleep = sessions.stream()
                .flatMap(Nights::nightsCoveredBy)
                .collect(Collectors.toSet());
        long sleeplessNightCount = Nights.nightsOfLog(sessions)
                .filter(Predicate.not(nightsWithSleep::contains))
                .count();
        return new SleepAnalysisResult<>(DESCRIPTION, sleeplessNightCount);
    }
}
