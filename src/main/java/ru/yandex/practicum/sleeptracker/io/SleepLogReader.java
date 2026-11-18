package ru.yandex.practicum.sleeptracker.io;

import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public final class SleepLogReader {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.uu HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final String DATE_TIME_FORMAT_HINT = "дд.мм.гг чч:мм";
    private static final String FIELD_SEPARATOR = ";";
    private static final int FIELD_COUNT = 3;

    private SleepLogReader() {
        throw new UnsupportedOperationException("Нельзя создать экземпляр утилитного класса SleepLogReader");
    }

    public static List<SleepingSession> read(Path path, ZoneId zone) throws IOException {
        try (Stream<String> lines = Files.lines(path)) {
            return lines.map(String::strip)
                    .filter(line -> !line.isEmpty())
                    .map(line -> parseLine(line, zone))
                    .toList();
        }
    }

    public static SleepingSession parseLine(String line, ZoneId zone) {
        String[] fields = line.strip().split(FIELD_SEPARATOR);
        if (fields.length != FIELD_COUNT) {
            throw new IllegalArgumentException(invalidLineMessage(line,
                    "Ожидается " + FIELD_COUNT + " поля через «" + FIELD_SEPARATOR + "»"));
        }
        try {
            return new SleepingSession(
                    parseDateTime(fields[0]),
                    parseDateTime(fields[1]),
                    parseQuality(fields[2]),
                    zone);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(invalidLineMessage(line, e.getMessage()), e);
        }
    }

    private static LocalDateTime parseDateTime(String field) {
        String value = field.strip();
        try {
            return LocalDateTime.parse(value, DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Дата «" + value + "» не соответствует формату " + DATE_TIME_FORMAT_HINT, e);
        }
    }

    private static SleepQuality parseQuality(String field) {
        String value = field.strip();
        try {
            return SleepQuality.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неизвестное качество сна «" + value + "», допустимые значения: "
                    + Arrays.toString(SleepQuality.values()), e);
        }
    }

    private static String invalidLineMessage(String line, String reason) {
        return "Некорректная строка лога «" + line.strip() + "». " + reason;
    }
}
