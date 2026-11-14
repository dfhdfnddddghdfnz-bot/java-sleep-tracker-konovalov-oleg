package ru.yandex.practicum.sleeptracker.analysis;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.sleeptracker.TestSessions.TASK_EXAMPLE_LOG;
import static ru.yandex.practicum.sleeptracker.TestSessions.session;
import static ru.yandex.practicum.sleeptracker.model.SleepQuality.BAD;
import static ru.yandex.practicum.sleeptracker.model.SleepQuality.GOOD;
import static ru.yandex.practicum.sleeptracker.model.SleepQuality.NORMAL;

class BadSessionCountFunctionTest {

    private final BadSessionCountFunction function = new BadSessionCountFunction();

    @Test
    void apply_emptyLog_returnsZero() {
        SleepAnalysisResult<Long> result = function.apply(List.of());

        assertAll(
                () -> assertEquals(BadSessionCountFunction.DESCRIPTION, result.description()),
                () -> assertEquals(0L, result.value()));
    }

    @Test
    void apply_onlyGoodAndNormalSessions_returnsZero() {
        SleepAnalysisResult<Long> result = function.apply(List.of(
                session("01.10.25 22:15", "02.10.25 08:00", GOOD),
                session("02.10.25 23:00", "03.10.25 08:00", NORMAL)));

        assertAll(
                () -> assertEquals(BadSessionCountFunction.DESCRIPTION, result.description()),
                () -> assertEquals(0L, result.value()));
    }

    @Test
    void apply_taskExampleLog_countsSingleBadSession() {
        SleepAnalysisResult<Long> result = function.apply(TASK_EXAMPLE_LOG);

        assertAll(
                () -> assertEquals(BadSessionCountFunction.DESCRIPTION, result.description()),
                () -> assertEquals(1L, result.value()));
    }

    @Test
    void apply_badNightAndBadDaytimeSessions_countsBoth() {
        SleepAnalysisResult<Long> result = function.apply(List.of(
                session("01.10.25 22:15", "02.10.25 08:00", BAD),
                session("02.10.25 14:30", "02.10.25 15:20", BAD),
                session("02.10.25 23:00", "03.10.25 08:00", GOOD)));

        assertAll(
                () -> assertEquals(BadSessionCountFunction.DESCRIPTION, result.description()),
                () -> assertEquals(2L, result.value()));
    }
}
