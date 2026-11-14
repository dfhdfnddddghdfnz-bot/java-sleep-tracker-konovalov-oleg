package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class TestSessions {

    public static final ZoneId ZONE = ZoneOffset.UTC;

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public static final List<SleepingSession> TASK_EXAMPLE_LOG = List.of(
            session("01.10.25 22:15", "02.10.25 08:00", SleepQuality.GOOD),
            session("02.10.25 23:00", "03.10.25 08:00", SleepQuality.NORMAL),
            session("03.10.25 14:30", "03.10.25 15:20", SleepQuality.NORMAL),
            session("03.10.25 23:30", "04.10.25 06:20", SleepQuality.BAD));

    private TestSessions() {
        throw new UnsupportedOperationException("Нельзя создать экземпляр утилитного класса TestSessions");
    }

    public static SleepingSession session(String start, String end, SleepQuality quality) {
        return new SleepingSession(dateTime(start), dateTime(end), quality, ZONE);
    }

    public static SleepingSession session(String start, String end) {
        return session(start, end, SleepQuality.GOOD);
    }

    public static LocalDateTime dateTime(String value) {
        return LocalDateTime.parse(value, DATE_TIME_FORMAT);
    }
}
