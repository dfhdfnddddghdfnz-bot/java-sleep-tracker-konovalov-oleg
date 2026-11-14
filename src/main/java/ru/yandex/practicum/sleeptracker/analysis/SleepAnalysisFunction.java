package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.util.List;
import java.util.function.Function;

@FunctionalInterface
public interface SleepAnalysisFunction<T> extends Function<List<SleepingSession>, SleepAnalysisResult<T>> {
}
