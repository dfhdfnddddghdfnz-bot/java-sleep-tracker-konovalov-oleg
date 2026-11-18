package ru.yandex.practicum.sleeptracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static java.util.Objects.requireNonNull;

public record SleepingSession(LocalDateTime start, LocalDateTime end, SleepQuality quality, ZoneId zone) {

    public SleepingSession {
        requireNonNull(start, "Время засыпания должно быть задано");
        requireNonNull(end, "Время пробуждения должно быть задано");
        requireNonNull(quality, "Качество сна должно быть задано");
        requireNonNull(zone, "Часовой пояс должен быть задан");
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("Пробуждение " + end + " должно быть позже засыпания " + start);
        }
    }

    public long durationMinutes() {
        return Duration.between(start.atZone(zone), end.atZone(zone)).toMinutes();
    }
}
