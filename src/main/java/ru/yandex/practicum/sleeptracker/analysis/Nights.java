package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

final class Nights {

    private static final LocalTime NIGHT_START = LocalTime.MIDNIGHT;
    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);
    private static final LocalTime FIRST_NIGHT_SWITCH_TIME = LocalTime.NOON;

    private Nights() {
        throw new UnsupportedOperationException("Нельзя создать экземпляр утилитного класса Nights");
    }

    static Stream<LocalDate> nightsCoveredBy(SleepingSession session) {
        return session.start().toLocalDate()
                .datesUntil(session.end().toLocalDate().plusDays(1))
                .filter(night -> overlapsNight(session, night));
    }

    static boolean coversAnyNight(SleepingSession session) {
        return nightsCoveredBy(session).findAny().isPresent();
    }

    static LocalDate firstNightCoveredBy(SleepingSession session) {
        return nightsCoveredBy(session)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Сессия " + session + " не пересекает ни одной ночи"));
    }

    static Stream<LocalDate> nightsOfLog(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return Stream.empty();
        }
        LocalDate firstNight = firstNightOfLog(sessions);
        LocalDate lastNight = lastNightOfLog(sessions);
        if (firstNight.isAfter(lastNight)) {
            return Stream.empty();
        }
        return firstNight.datesUntil(lastNight.plusDays(1));
    }

    private static boolean overlapsNight(SleepingSession session, LocalDate night) {
        return session.start().isBefore(night.atTime(NIGHT_END))
                && session.end().isAfter(night.atTime(NIGHT_START));
    }

    private static LocalDate firstNightOfLog(List<SleepingSession> sessions) {
        LocalDateTime logStart = sessions.stream()
                .map(SleepingSession::start)
                .min(Comparator.naturalOrder())
                .orElseThrow();
        LocalDate logStartDate = logStart.toLocalDate();
        return logStart.toLocalTime().isBefore(FIRST_NIGHT_SWITCH_TIME) ? logStartDate : logStartDate.plusDays(1);
    }

    private static LocalDate lastNightOfLog(List<SleepingSession> sessions) {
        LocalDateTime logEnd = sessions.stream()
                .map(SleepingSession::end)
                .max(Comparator.naturalOrder())
                .orElseThrow();
        LocalDate logEndDate = logEnd.toLocalDate();
        return logEnd.toLocalTime().equals(NIGHT_START) ? logEndDate.minusDays(1) : logEndDate;
    }
}
