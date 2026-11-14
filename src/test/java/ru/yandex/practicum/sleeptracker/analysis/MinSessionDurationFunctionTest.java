package ru.yandex.practicum.sleeptracker.analysis;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.sleeptracker.TestSessions.TASK_EXAMPLE_LOG;
import static ru.yandex.practicum.sleeptracker.TestSessions.session;

class MinSessionDurationFunctionTest {

    private final MinSessionDurationFunction function = new MinSessionDurationFunction();

    @Test
    void apply_emptyLog_returnsZero() {
        SleepAnalysisResult<Long> result = function.apply(List.of());

        assertAll(
                () -> assertEquals(MinSessionDurationFunction.DESCRIPTION, result.description()),
                () -> assertEquals(0L, result.value()));
    }

    @Test
    void apply_singleOvernightSession_returnsItsDuration() {
        SleepAnalysisResult<Long> result = function.apply(List.of(session("01.10.25 23:15", "02.10.25 07:30")));

        assertAll(
                () -> assertEquals(MinSessionDurationFunction.DESCRIPTION, result.description()),
                () -> assertEquals(495L, result.value()));
    }

    @Test
    void apply_nightAndDaytimeSessions_returnsShortestDuration() {
        SleepAnalysisResult<Long> result = function.apply(TASK_EXAMPLE_LOG);

        assertAll(
                () -> assertEquals(MinSessionDurationFunction.DESCRIPTION, result.description()),
                () -> assertEquals(50L, result.value()));
    }
}
