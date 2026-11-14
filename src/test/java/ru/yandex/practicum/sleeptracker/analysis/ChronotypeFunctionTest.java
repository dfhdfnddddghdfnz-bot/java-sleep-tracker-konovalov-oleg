package ru.yandex.practicum.sleeptracker.analysis;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.model.Chronotype;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.sleeptracker.TestSessions.session;
import static ru.yandex.practicum.sleeptracker.model.Chronotype.LARK;
import static ru.yandex.practicum.sleeptracker.model.Chronotype.OWL;
import static ru.yandex.practicum.sleeptracker.model.Chronotype.PIGEON;

class ChronotypeFunctionTest {

    private final ChronotypeFunction function = new ChronotypeFunction();

    @Test
    void apply_emptyLog_returnsPigeon() {
        SleepAnalysisResult<Chronotype> result = function.apply(List.of());

        assertAll(
                () -> assertEquals(ChronotypeFunction.DESCRIPTION, result.description()),
                () -> assertEquals(PIGEON, result.value()));
    }

    @Test
    void apply_mostlyOwlNights_returnsOwl() {
        SleepAnalysisResult<Chronotype> result = function.apply(List.of(
                session("01.10.25 23:30", "02.10.25 09:30"),
                session("03.10.25 00:30", "03.10.25 10:00"),
                session("03.10.25 22:30", "04.10.25 08:00")));

        assertAll(
                () -> assertEquals(ChronotypeFunction.DESCRIPTION, result.description()),
                () -> assertEquals(OWL, result.value()));
    }

    @Test
    void apply_mostlyLarkNights_returnsLark() {
        SleepAnalysisResult<Chronotype> result = function.apply(List.of(
                session("01.10.25 21:00", "02.10.25 05:30"),
                session("02.10.25 21:30", "03.10.25 06:30"),
                session("03.10.25 23:30", "04.10.25 09:30")));

        assertAll(
                () -> assertEquals(ChronotypeFunction.DESCRIPTION, result.description()),
                () -> assertEquals(LARK, result.value()));
    }

    @Test
    void apply_mostlyPigeonNights_returnsPigeon() {
        SleepAnalysisResult<Chronotype> result = function.apply(List.of(
                session("01.10.25 22:30", "02.10.25 07:30"),
                session("02.10.25 22:45", "03.10.25 08:00"),
                session("03.10.25 23:30", "04.10.25 09:30")));

        assertAll(
                () -> assertEquals(ChronotypeFunction.DESCRIPTION, result.description()),
                () -> assertEquals(PIGEON, result.value()));
    }

    @Test
    void apply_tieBetweenLeadingChronotypes_returnsPigeon() {
        List<SleepingSession> owlAndLark = List.of(
                session("01.10.25 23:30", "02.10.25 09:30"),
                session("02.10.25 21:00", "03.10.25 05:30"));
        List<SleepingSession> twoOwlsTwoLarksOnePigeon = List.of(
                session("01.10.25 23:30", "02.10.25 09:30"),
                session("02.10.25 23:30", "03.10.25 09:30"),
                session("03.10.25 21:00", "04.10.25 05:30"),
                session("04.10.25 21:00", "05.10.25 05:30"),
                session("05.10.25 22:30", "06.10.25 07:30"));

        assertAll(
                () -> assertEquals(PIGEON, function.apply(owlAndLark).value()),
                () -> assertEquals(PIGEON, function.apply(twoOwlsTwoLarksOnePigeon).value()));
    }

    @Test
    void apply_fallAsleepAfterMidnightAndWakeUpLate_returnsOwl() {
        SleepAnalysisResult<Chronotype> result = function.apply(List.of(session("02.10.25 01:00", "02.10.25 10:30")));

        assertAll(
                () -> assertEquals(ChronotypeFunction.DESCRIPTION, result.description()),
                () -> assertEquals(OWL, result.value()));
    }

    @Test
    void apply_daytimeNapsAndSleeplessNights_areIgnored() {
        SleepAnalysisResult<Chronotype> result = function.apply(List.of(
                session("01.10.25 23:30", "02.10.25 09:30"),
                session("02.10.25 14:00", "02.10.25 15:00"),
                session("03.10.25 13:00", "03.10.25 14:00")));

        assertAll(
                () -> assertEquals(ChronotypeFunction.DESCRIPTION, result.description()),
                () -> assertEquals(OWL, result.value()));
    }

    @Test
    void apply_wokeUpAndFellAsleepAgainWithinNight_classifiesWholeNight() {
        SleepAnalysisResult<Chronotype> result = function.apply(List.of(
                session("01.10.25 23:30", "02.10.25 03:00"),
                session("02.10.25 03:30", "02.10.25 09:30")));

        assertAll(
                () -> assertEquals(ChronotypeFunction.DESCRIPTION, result.description()),
                () -> assertEquals(OWL, result.value()));
    }

    @Test
    void apply_timesExactlyOnThresholds_returnsPigeon() {
        assertAll(
                () -> assertEquals(PIGEON,
                        function.apply(List.of(session("01.10.25 23:00", "02.10.25 09:30"))).value()),
                () -> assertEquals(PIGEON,
                        function.apply(List.of(session("01.10.25 23:30", "02.10.25 09:00"))).value()),
                () -> assertEquals(PIGEON,
                        function.apply(List.of(session("01.10.25 22:00", "02.10.25 06:30"))).value()),
                () -> assertEquals(PIGEON,
                        function.apply(List.of(session("01.10.25 21:00", "02.10.25 07:00"))).value()));
    }
}
