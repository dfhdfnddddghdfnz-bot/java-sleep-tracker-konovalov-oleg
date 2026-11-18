package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.util.List;

public class SessionCountFunction implements SleepAnalysisFunction<Long> {

    public static final String DESCRIPTION = "Количество сессий сна";

    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        return new SleepAnalysisResult<>(DESCRIPTION, (long) sessions.size());
    }
}
