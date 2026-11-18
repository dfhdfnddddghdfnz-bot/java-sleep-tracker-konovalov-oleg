package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.analysis.AverageSessionDurationFunction;
import ru.yandex.practicum.sleeptracker.analysis.BadSessionCountFunction;
import ru.yandex.practicum.sleeptracker.analysis.ChronotypeFunction;
import ru.yandex.practicum.sleeptracker.analysis.MaxSessionDurationFunction;
import ru.yandex.practicum.sleeptracker.analysis.MinSessionDurationFunction;
import ru.yandex.practicum.sleeptracker.analysis.SessionCountFunction;
import ru.yandex.practicum.sleeptracker.analysis.SleepAnalysisFunction;
import ru.yandex.practicum.sleeptracker.analysis.SleeplessNightsFunction;
import ru.yandex.practicum.sleeptracker.io.SleepLogReader;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.List;

public class SleepTrackerApp {

    private static final List<SleepAnalysisFunction<?>> ANALYSIS_FUNCTIONS = List.of(
            new SessionCountFunction(),
            new MinSessionDurationFunction(),
            new MaxSessionDurationFunction(),
            new AverageSessionDurationFunction(),
            new BadSessionCountFunction(),
            new SleeplessNightsFunction(),
            new ChronotypeFunction());

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Укажите путь к файлу с логом сна: java " + SleepTrackerApp.class.getName()
                    + " <путь к файлу>");
            return;
        }
        Path logPath = Path.of(args[0]);
        List<SleepingSession> sessions;
        try {
            sessions = SleepLogReader.read(logPath, ZoneId.systemDefault());
        } catch (NoSuchFileException e) {
            System.err.println("Файл " + logPath.toAbsolutePath() + " не найден");
            return;
        } catch (IOException | UncheckedIOException e) {
            System.err.println("Не удалось прочитать файл " + logPath + ": " + e.getMessage());
            return;
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка в файле " + logPath + ". " + e.getMessage());
            return;
        }
        if (sessions.isEmpty()) {
            System.out.println("Лог сна " + logPath + " пуст, анализировать нечего");
            return;
        }
        System.out.println("Анализ сна по файлу " + logPath);
        ANALYSIS_FUNCTIONS.stream()
                .map(function -> function.apply(sessions))
                .forEach(result -> System.out.println(result.description() + ": " + result.value()));
    }
}
