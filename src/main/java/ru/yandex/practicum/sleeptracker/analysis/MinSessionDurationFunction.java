package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.util.List;

public class MinSessionDurationFunction implements SleepAnalysisFunction<Long> {

    public static final String DESCRIPTION = "Минимальная продолжительность сессии, мин";

    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        long minDurationMinutes = sessions.stream()
                .mapToLong(SleepingSession::durationMinutes)
                .min()
                .orElse(0);
        return new SleepAnalysisResult<>(DESCRIPTION, minDurationMinutes);
    }
}
