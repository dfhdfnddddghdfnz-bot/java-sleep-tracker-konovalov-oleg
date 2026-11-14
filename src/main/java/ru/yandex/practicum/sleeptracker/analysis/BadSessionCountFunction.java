package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.util.List;

public class BadSessionCountFunction implements SleepAnalysisFunction<Long> {

    public static final String DESCRIPTION = "Количество сессий с плохим качеством сна";

    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        long badSessionCount = sessions.stream()
                .filter(session -> session.quality() == SleepQuality.BAD)
                .count();
        return new SleepAnalysisResult<>(DESCRIPTION, badSessionCount);
    }
}
