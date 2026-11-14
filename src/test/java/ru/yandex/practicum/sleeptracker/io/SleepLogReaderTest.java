package ru.yandex.practicum.sleeptracker.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.sleeptracker.TestSessions.ZONE;
import static ru.yandex.practicum.sleeptracker.TestSessions.dateTime;
import static ru.yandex.practicum.sleeptracker.TestSessions.session;

class SleepLogReaderTest {

    @Test
    void parseLine_validLine_returnsSessionWithAllFields() {
        SleepingSession session = SleepLogReader.parseLine("01.10.25 22:15;02.10.25 08:00;GOOD", ZONE);

        assertAll(
                () -> assertEquals(dateTime("01.10.25 22:15"), session.start()),
                () -> assertEquals(dateTime("02.10.25 08:00"), session.end()),
                () -> assertEquals(SleepQuality.GOOD, session.quality()),
                () -> assertEquals(ZONE, session.zone()));
    }

    @Test
    void parseLine_twoDigitYear_resolvesToYearsFrom2000To2099() {
        SleepingSession firstYear = SleepLogReader.parseLine("01.01.00 13:00;01.01.00 14:00;NORMAL", ZONE);
        SleepingSession lastYear = SleepLogReader.parseLine("31.12.99 13:00;31.12.99 14:00;NORMAL", ZONE);

        assertAll(
                () -> assertEquals(2000, firstYear.start().getYear()),
                () -> assertEquals(2099, lastYear.start().getYear()));
    }

    @Test
    void parseLine_surroundingWhitespace_isIgnored() {
        SleepingSession session = SleepLogReader.parseLine(" 03.10.25 23:30 ; 04.10.25 06:20 ; BAD \t", ZONE);

        assertAll(
                () -> assertEquals(dateTime("03.10.25 23:30"), session.start()),
                () -> assertEquals(dateTime("04.10.25 06:20"), session.end()),
                () -> assertEquals(SleepQuality.BAD, session.quality()));
    }

    @Test
    void parseLine_malformedLine_throwsIllegalArgumentException() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> SleepLogReader.parseLine("01.10.25 22:15;02.10.25 08:00", ZONE)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> SleepLogReader.parseLine("01.10.25 22:15;02.10.25 08:00;GOOD;BAD", ZONE)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> SleepLogReader.parseLine("01.10.25 22:15;02.10.25 08:00;EXCELLENT", ZONE)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> SleepLogReader.parseLine("2025-10-01 22:15;02.10.25 08:00;GOOD", ZONE)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> SleepLogReader.parseLine("30.02.25 22:15;01.03.25 08:00;GOOD", ZONE)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> SleepLogReader.parseLine("02.10.25 08:00;01.10.25 22:15;GOOD", ZONE)));
    }

    @Test
    void parseLine_invalidDateOrQuality_messageNamesLineAndWrongValue() {
        String lineWithWrongDate = "01.10.25 22:15;2025-10-02 08:00;GOOD";
        String lineWithWrongQuality = "01.10.25 22:15;02.10.25 08:00;SUPER";

        String wrongDateMessage = assertThrows(IllegalArgumentException.class,
                () -> SleepLogReader.parseLine(lineWithWrongDate, ZONE)).getMessage();
        String wrongQualityMessage = assertThrows(IllegalArgumentException.class,
                () -> SleepLogReader.parseLine(lineWithWrongQuality, ZONE)).getMessage();

        assertAll(
                () -> assertTrue(wrongDateMessage.contains(lineWithWrongDate)),
                () -> assertTrue(wrongDateMessage.contains("Дата «2025-10-02 08:00»")),
                () -> assertTrue(wrongQualityMessage.contains(lineWithWrongQuality)),
                () -> assertTrue(wrongQualityMessage.contains("Неизвестное качество сна «SUPER»")),
                () -> assertTrue(wrongQualityMessage.contains("[GOOD, NORMAL, BAD]")));
    }

    @Test
    void read_fileWithBlankLines_returnsSessionsInFileOrder(@TempDir Path directory) throws IOException {
        Path log = Files.writeString(directory.resolve("sleep_log.txt"), """
                01.10.25 22:15;02.10.25 08:00;GOOD

                03.10.25 14:30;03.10.25 15:20;NORMAL
                03.10.25 23:30;04.10.25 06:20;BAD\s

                """);

        List<SleepingSession> sessions = SleepLogReader.read(log, ZONE);

        assertEquals(List.of(
                        session("01.10.25 22:15", "02.10.25 08:00", SleepQuality.GOOD),
                        session("03.10.25 14:30", "03.10.25 15:20", SleepQuality.NORMAL),
                        session("03.10.25 23:30", "04.10.25 06:20", SleepQuality.BAD)),
                sessions);
    }

    @Test
    void read_emptyFileAndMissingFile_returnsEmptyListOrThrows(@TempDir Path directory) throws IOException {
        Path emptyLog = Files.writeString(directory.resolve("empty.txt"), "");
        Path missingLog = directory.resolve("missing.txt");

        assertAll(
                () -> assertEquals(List.of(), SleepLogReader.read(emptyLog, ZONE)),
                () -> assertThrows(NoSuchFileException.class, () -> SleepLogReader.read(missingLog, ZONE)));
    }
}
