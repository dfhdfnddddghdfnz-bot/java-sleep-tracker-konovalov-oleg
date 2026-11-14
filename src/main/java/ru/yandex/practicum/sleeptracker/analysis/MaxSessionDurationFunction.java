package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.util.List;

public class MaxSessionDurationFunction implements SleepAnalysisFunction<Long> {

    public static final String DESCRIPTION = "Максимальная продолжительность сессии, мин";

    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        long maxDurationMinutes = sessions.stream()
                .mapToLong(SleepingSession::durationMinutes)
                .max()
                .orElse(0);
        return new SleepAnalysisResult<>(DESCRIPTION, maxDurationMinutes);
    }
}
