package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.util.List;

public class AverageSessionDurationFunction implements SleepAnalysisFunction<Long> {

    public static final String DESCRIPTION = "Средняя продолжительность сессии, мин";

    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        double averageDurationMinutes = sessions.stream()
                .mapToLong(SleepingSession::durationMinutes)
                .average()
                .orElse(0);
        return new SleepAnalysisResult<>(DESCRIPTION, Math.round(averageDurationMinutes));
    }
}
