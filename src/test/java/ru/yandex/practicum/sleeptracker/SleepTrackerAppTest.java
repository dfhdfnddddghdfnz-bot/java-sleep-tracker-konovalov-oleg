package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.practicum.sleeptracker.analysis.AverageSessionDurationFunction;
import ru.yandex.practicum.sleeptracker.analysis.BadSessionCountFunction;
import ru.yandex.practicum.sleeptracker.analysis.ChronotypeFunction;
import ru.yandex.practicum.sleeptracker.analysis.MaxSessionDurationFunction;
import ru.yandex.practicum.sleeptracker.analysis.MinSessionDurationFunction;
import ru.yandex.practicum.sleeptracker.analysis.SessionCountFunction;
import ru.yandex.practicum.sleeptracker.analysis.SleeplessNightsFunction;
import ru.yandex.practicum.sleeptracker.model.Chronotype;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SleepTrackerAppTest {

    private static final String SAMPLE_LOG = """
            01.10.25 23:15;02.10.25 07:30;GOOD
            02.10.25 23:50;03.10.25 06:40;NORMAL
            03.10.25 14:10;03.10.25 15:00;NORMAL
            03.10.25 23:40;04.10.25 08:00;BAD
            05.10.25 00:10;05.10.25 06:20;GOOD
            05.10.25 13:30;05.10.25 14:15;NORMAL
            06.10.25 22:30;07.10.25 05:50;GOOD
            07.10.25 23:45;08.10.25 06:30;GOOD
            08.10.25 23:50;09.10.25 07:10;GOOD
            10.10.25 13:00;10.10.25 14:30;NORMAL
            10.10.25 23:55;11.10.25 06:10;GOOD
            11.10.25 23:10;12.10.25 07:00;BAD
            30.10.25 23:50;31.10.25 06:30;GOOD
            """;

    private final ByteArrayOutputStream standardOutput = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorOutput = new ByteArrayOutputStream();
    private PrintStream originalStandardOutput;
    private PrintStream originalErrorOutput;

    @BeforeEach
    void captureConsoleOutput() {
        originalStandardOutput = System.out;
        originalErrorOutput = System.err;
        System.setOut(new PrintStream(standardOutput, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(errorOutput, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreConsoleOutput() {
        System.setOut(originalStandardOutput);
        System.setErr(originalErrorOutput);
    }

    @Test
    void main_sampleLog_printsResultOfEveryFunctionInOrder(@TempDir Path directory) throws IOException {
        Path log = Files.writeString(directory.resolve("sleep_log.txt"), SAMPLE_LOG);

        SleepTrackerApp.main(new String[]{log.toString()});

        assertAll(
                () -> assertEquals(List.of(
                                "Анализ сна по файлу " + log,
                                SessionCountFunction.DESCRIPTION + ": 13",
                                MinSessionDurationFunction.DESCRIPTION + ": 45",
                                MaxSessionDurationFunction.DESCRIPTION + ": 500",
                                AverageSessionDurationFunction.DESCRIPTION + ": 345",
                                BadSessionCountFunction.DESCRIPTION + ": 2",
                                SleeplessNightsFunction.DESCRIPTION + ": 20",
                                ChronotypeFunction.DESCRIPTION + ": " + Chronotype.PIGEON),
                        standardOutput.toString(StandardCharsets.UTF_8).lines().toList()),
                () -> assertEquals("", errorOutput.toString(StandardCharsets.UTF_8)));
    }

    @Test
    void main_withoutArguments_printsUsageToErrorOutput() {
        SleepTrackerApp.main(new String[0]);

        assertAll(
                () -> assertEquals("", standardOutput.toString(StandardCharsets.UTF_8)),
                () -> assertTrue(errorOutput.toString(StandardCharsets.UTF_8)
                        .startsWith("Укажите путь к файлу с логом сна")));
    }

    @Test
    void main_missingFile_printsFileNotFoundToErrorOutput(@TempDir Path directory) {
        Path missingLog = directory.resolve("missing.txt");

        SleepTrackerApp.main(new String[]{missingLog.toString()});

        assertAll(
                () -> assertEquals("", standardOutput.toString(StandardCharsets.UTF_8)),
                () -> assertTrue(errorOutput.toString(StandardCharsets.UTF_8).contains("не найден")));
    }

    @Test
    void main_malformedLog_printsWrongLineToErrorOutput(@TempDir Path directory) throws IOException {
        Path log = Files.writeString(directory.resolve("sleep_log.txt"), "01.10.25 22:15;02.10.25 08:00;SUPER");

        SleepTrackerApp.main(new String[]{log.toString()});

        assertAll(
                () -> assertEquals("", standardOutput.toString(StandardCharsets.UTF_8)),
                () -> assertTrue(errorOutput.toString(StandardCharsets.UTF_8)
                        .contains("Неизвестное качество сна «SUPER»")));
    }

    @Test
    void main_emptyLog_reportsNothingToAnalyze(@TempDir Path directory) throws IOException {
        Path log = Files.writeString(directory.resolve("sleep_log.txt"), "");

        SleepTrackerApp.main(new String[]{log.toString()});

        assertAll(
                () -> assertTrue(standardOutput.toString(StandardCharsets.UTF_8).contains("пуст")),
                () -> assertEquals("", errorOutput.toString(StandardCharsets.UTF_8)));
    }
}
