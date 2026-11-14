package ru.yandex.practicum.sleeptracker.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.sleeptracker.TestSessions.ZONE;
import static ru.yandex.practicum.sleeptracker.TestSessions.dateTime;
import static ru.yandex.practicum.sleeptracker.TestSessions.session;
import static ru.yandex.practicum.sleeptracker.model.SleepQuality.GOOD;

class SleepingSessionTest {

    private static final ZoneId BERLIN = ZoneId.of("Europe/Berlin");

    @Test
    void durationMinutes_sessionsWithinDayAndAcrossMidnight_returnsWholeMinutes() {
        assertAll(
                () -> assertEquals(50, session("03.10.25 14:10", "03.10.25 15:00").durationMinutes()),
                () -> assertEquals(495, session("01.10.25 23:15", "02.10.25 07:30").durationMinutes()),
                () -> assertEquals(1440 * 31 + 60, session("30.09.25 23:00", "01.11.25 00:00").durationMinutes()));
    }

    @Test
    void durationMinutes_nightOfDaylightSavingTransition_returnsElapsedMinutes() {
        SleepingSession clocksTurnedBack = new SleepingSession(
                dateTime("25.10.25 23:00"), dateTime("26.10.25 07:00"), GOOD, BERLIN);
        SleepingSession clocksTurnedForward = new SleepingSession(
                dateTime("29.03.25 23:00"), dateTime("30.03.25 07:00"), GOOD, BERLIN);
        SleepingSession sameNightWithoutTransition = new SleepingSession(
                dateTime("25.10.25 23:00"), dateTime("26.10.25 07:00"), GOOD, ZONE);

        assertAll(
                () -> assertEquals(540, clocksTurnedBack.durationMinutes()),
                () -> assertEquals(420, clocksTurnedForward.durationMinutes()),
                () -> assertEquals(480, sameNightWithoutTransition.durationMinutes()));
    }

    @Test
    void constructor_endNotAfterStart_throwsIllegalArgumentException() {
        LocalDateTime morning = dateTime("02.10.25 08:00");
        LocalDateTime earlierMorning = dateTime("02.10.25 07:00");

        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new SleepingSession(morning, earlierMorning, GOOD, ZONE)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new SleepingSession(morning, morning, GOOD, ZONE)));
    }

    @Test
    void constructor_missingField_throwsNullPointerException() {
        LocalDateTime evening = dateTime("01.10.25 23:00");
        LocalDateTime morning = dateTime("02.10.25 08:00");

        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> new SleepingSession(null, morning, GOOD, ZONE)),
                () -> assertThrows(NullPointerException.class,
                        () -> new SleepingSession(evening, null, GOOD, ZONE)),
                () -> assertThrows(NullPointerException.class,
                        () -> new SleepingSession(evening, morning, null, ZONE)),
                () -> assertThrows(NullPointerException.class,
                        () -> new SleepingSession(evening, morning, GOOD, null)));
    }
}
