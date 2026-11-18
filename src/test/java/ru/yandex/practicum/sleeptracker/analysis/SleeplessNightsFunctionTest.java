package ru.yandex.practicum.sleeptracker.analysis;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.sleeptracker.TestSessions.TASK_EXAMPLE_LOG;
import static ru.yandex.practicum.sleeptracker.TestSessions.session;

class SleeplessNightsFunctionTest {

    private final SleeplessNightsFunction function = new SleeplessNightsFunction();

    @Test
    void apply_emptyLog_returnsZero() {
        SleepAnalysisResult<Long> result = function.apply(List.of());

        assertAll(
                () -> assertEquals(SleeplessNightsFunction.DESCRIPTION, result.description()),
                () -> assertEquals(0L, result.value()));
    }

    @Test
    void apply_everyNightCoveredDespiteDaytimeNap_returnsZero() {
        SleepAnalysisResult<Long> result = function.apply(TASK_EXAMPLE_LOG);

        assertAll(
                () -> assertEquals(SleeplessNightsFunction.DESCRIPTION, result.description()),
                () -> assertEquals(0L, result.value()));
    }

    @Test
    void apply_sessionsBeforeAfterAndOutsideNightInterval_countsOnlyNightWithoutOverlap() {
        SleepAnalysisResult<Long> result = function.apply(List.of(
                session("01.10.25 23:00", "02.10.25 03:00"),
                session("03.10.25 02:00", "03.10.25 07:00"),
                session("04.10.25 07:00", "04.10.25 11:00")));

        assertAll(
                () -> assertEquals(SleeplessNightsFunction.DESCRIPTION, result.description()),
                () -> assertEquals(1L, result.value()));
    }

    @Test
    void apply_severalSessionsWithinOneNight_countsThatNightOnce() {
        SleepAnalysisResult<Long> result = function.apply(List.of(
                session("01.10.25 23:00", "02.10.25 02:00"),
                session("02.10.25 02:30", "02.10.25 07:00"),
                session("03.10.25 23:00", "04.10.25 07:00")));

        assertAll(
                () -> assertEquals(SleeplessNightsFunction.DESCRIPTION, result.description()),
                () -> assertEquals(1L, result.value()));
    }

    @Test
    void apply_firstSessionStartsBeforeNoon_includesPreviousNight() {
        SleepAnalysisResult<Long> result = function.apply(List.of(
                session("05.10.25 08:00", "05.10.25 09:30"),
                session("05.10.25 23:00", "06.10.25 07:00")));

        assertAll(
                () -> assertEquals(SleeplessNightsFunction.DESCRIPTION, result.description()),
                () -> assertEquals(1L, result.value()));
    }

    @Test
    void apply_firstSessionStartsAtOrAfterNoon_startsFromNextNight() {
        assertAll(
                () -> assertEquals(0L, function.apply(List.of(
                        session("05.10.25 12:00", "05.10.25 13:00"),
                        session("05.10.25 23:00", "06.10.25 07:00"))).value()),
                () -> assertEquals(0L, function.apply(List.of(
                        session("05.10.25 14:00", "05.10.25 15:00"),
                        session("05.10.25 23:00", "06.10.25 07:00"))).value()));
    }

    @Test
    void apply_logSpanningMoreThanMonth_countsEveryNightBetweenMonths() {
        SleepAnalysisResult<Long> result = function.apply(List.of(
                session("25.09.25 23:00", "26.09.25 07:00"),
                session("30.10.25 23:00", "31.10.25 07:00")));

        assertAll(
                () -> assertEquals(SleeplessNightsFunction.DESCRIPTION, result.description()),
                () -> assertEquals(34L, result.value()));
    }

    @Test
    void apply_sessionsTouchingNightBoundariesOnly_doNotCoverNight() {
        SleepAnalysisResult<Long> result = function.apply(List.of(
                session("01.10.25 23:00", "02.10.25 07:00"),
                session("02.10.25 18:00", "03.10.25 00:00"),
                session("03.10.25 06:00", "03.10.25 10:00"),
                session("03.10.25 23:00", "04.10.25 07:00")));

        assertAll(
                () -> assertEquals(SleeplessNightsFunction.DESCRIPTION, result.description()),
                () -> assertEquals(1L, result.value()));
    }

    @Test
    void apply_logEndsExactlyAtMidnight_ignoresNightAfterLogEnd() {
        SleepAnalysisResult<Long> result = function.apply(List.of(
                session("01.10.25 23:00", "02.10.25 07:00"),
                session("02.10.25 20:00", "03.10.25 00:00")));

        assertAll(
                () -> assertEquals(SleeplessNightsFunction.DESCRIPTION, result.description()),
                () -> assertEquals(0L, result.value()));
    }

    @Test
    void apply_onlyDaytimeNap_returnsZero() {
        SleepAnalysisResult<Long> result = function.apply(List.of(session("05.10.25 14:00", "05.10.25 15:00")));

        assertAll(
                () -> assertEquals(SleeplessNightsFunction.DESCRIPTION, result.description()),
                () -> assertEquals(0L, result.value()));
    }

    @Test
    void apply_sessionSpanningTwoNights_coversBoth() {
        SleepAnalysisResult<Long> result = function.apply(List.of(
                session("01.10.25 23:00", "03.10.25 07:00"),
                session("03.10.25 23:00", "04.10.25 07:00")));

        assertAll(
                () -> assertEquals(SleeplessNightsFunction.DESCRIPTION, result.description()),
                () -> assertEquals(0L, result.value()));
    }
}
