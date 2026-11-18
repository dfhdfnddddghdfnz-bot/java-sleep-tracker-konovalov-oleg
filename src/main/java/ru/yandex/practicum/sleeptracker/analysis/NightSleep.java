package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.time.LocalDate;
import java.time.LocalDateTime;

record NightSleep(LocalDate night, LocalDateTime fallAsleep, LocalDateTime wakeUp) {

    static NightSleep of(SleepingSession session) {
        return new NightSleep(Nights.firstNightCoveredBy(session), session.start(), session.end());
    }

    NightSleep merge(NightSleep sameNight) {
        LocalDateTime earliestFallAsleep = fallAsleep.isBefore(sameNight.fallAsleep)
                ? fallAsleep
                : sameNight.fallAsleep;
        LocalDateTime latestWakeUp = wakeUp.isAfter(sameNight.wakeUp) ? wakeUp : sameNight.wakeUp;
        return new NightSleep(night, earliestFallAsleep, latestWakeUp);
    }
}
