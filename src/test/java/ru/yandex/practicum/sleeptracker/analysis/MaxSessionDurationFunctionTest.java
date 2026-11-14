package ru.yandex.practicum.sleeptracker.analysis;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.sleeptracker.TestSessions.TASK_EXAMPLE_LOG;
import static ru.yandex.practicum.sleeptracker.TestSessions.session;

class MaxSessionDurationFunctionTest {

    private final MaxSessionDurationFunction function = new MaxSessionDurationFunction();

    @Test
    void apply_emptyLog_returnsZero() {
        SleepAnalysisResult<Long> result = function.apply(List.of());

        assertAll(
                () -> assertEquals(MaxSessionDurationFunction.DESCRIPTION, result.description()),
                () -> assertEquals(0L, result.value()));
    }

    @Test
    void apply_singleDaytimeSession_returnsItsDuration() {
        SleepAnalysisResult<Long> result = function.apply(List.of(session("03.10.25 14:30", "03.10.25 15:20")));

        assertAll(
                () -> assertEquals(MaxSessionDurationFunction.DESCRIPTION, result.description()),
                () -> assertEquals(50L, result.value()));
    }

    @Test
    void apply_nightAndDaytimeSessions_returnsLongestDurationAcrossMidnight() {
        SleepAnalysisResult<Long> result = function.apply(TASK_EXAMPLE_LOG);

        assertAll(
                () -> assertEquals(MaxSessionDurationFunction.DESCRIPTION, result.description()),
                () -> assertEquals(585L, result.value()));
    }
}
