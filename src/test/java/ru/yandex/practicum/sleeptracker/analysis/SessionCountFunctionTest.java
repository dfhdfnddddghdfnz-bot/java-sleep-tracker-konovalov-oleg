package ru.yandex.practicum.sleeptracker.analysis;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.sleeptracker.TestSessions.session;

class SessionCountFunctionTest {

    private final SessionCountFunction function = new SessionCountFunction();

    @Test
    void apply_emptyLog_returnsZero() {
        SleepAnalysisResult<Long> result = function.apply(List.of());

        assertAll(
                () -> assertEquals(SessionCountFunction.DESCRIPTION, result.description()),
                () -> assertEquals(0L, result.value()));
    }

    @Test
    void apply_singleSession_returnsOne() {
        SleepAnalysisResult<Long> result = function.apply(List.of(session("01.10.25 22:15", "02.10.25 08:00")));

        assertAll(
                () -> assertEquals(SessionCountFunction.DESCRIPTION, result.description()),
                () -> assertEquals(1L, result.value()));
    }

    @Test
    void apply_nightAndDaytimeSessions_countsEverySession() {
        List<SleepingSession> sessions = List.of(
                session("01.10.25 22:15", "02.10.25 08:00"),
                session("02.10.25 23:00", "03.10.25 08:00"),
                session("03.10.25 14:30", "03.10.25 15:20"),
                session("03.10.25 23:30", "04.10.25 06:20"));

        SleepAnalysisResult<Long> result = function.apply(sessions);

        assertAll(
                () -> assertEquals(SessionCountFunction.DESCRIPTION, result.description()),
                () -> assertEquals(4L, result.value()));
    }
}
