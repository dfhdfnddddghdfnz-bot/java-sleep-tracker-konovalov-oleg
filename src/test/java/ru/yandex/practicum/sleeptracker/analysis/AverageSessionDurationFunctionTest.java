package ru.yandex.practicum.sleeptracker.analysis;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.sleeptracker.TestSessions.TASK_EXAMPLE_LOG;
import static ru.yandex.practicum.sleeptracker.TestSessions.session;

class AverageSessionDurationFunctionTest {

    private final AverageSessionDurationFunction function = new AverageSessionDurationFunction();

    @Test
    void apply_emptyLog_returnsZero() {
        SleepAnalysisResult<Long> result = function.apply(List.of());

        assertAll(
                () -> assertEquals(AverageSessionDurationFunction.DESCRIPTION, result.description()),
                () -> assertEquals(0L, result.value()));
    }

    @Test
    void apply_singleSession_returnsItsDuration() {
        SleepAnalysisResult<Long> result = function.apply(List.of(session("02.10.25 23:00", "03.10.25 08:00")));

        assertAll(
                () -> assertEquals(AverageSessionDurationFunction.DESCRIPTION, result.description()),
                () -> assertEquals(540L, result.value()));
    }

    @Test
    void apply_nightAndDaytimeSessions_returnsMeanRoundedToWholeMinutes() {
        SleepAnalysisResult<Long> result = function.apply(TASK_EXAMPLE_LOG);

        assertAll(
                () -> assertEquals(AverageSessionDurationFunction.DESCRIPTION, result.description()),
                () -> assertEquals(396L, result.value()));
    }

    @Test
    void apply_meanEndingInHalfMinute_roundsUp() {
        SleepAnalysisResult<Long> result = function.apply(List.of(
                session("03.10.25 14:10", "03.10.25 15:00"),
                session("05.10.25 13:30", "05.10.25 14:15")));

        assertAll(
                () -> assertEquals(AverageSessionDurationFunction.DESCRIPTION, result.description()),
                () -> assertEquals(48L, result.value()));
    }
}
