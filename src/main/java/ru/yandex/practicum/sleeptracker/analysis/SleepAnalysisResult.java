package ru.yandex.practicum.sleeptracker.analysis;

import static java.util.Objects.requireNonNull;

public record SleepAnalysisResult<T>(String description, T value) {

    public SleepAnalysisResult {
        requireNonNull(description, "Описание результата должно быть задано");
        requireNonNull(value, "Значение результата должно быть задано");
    }
}
