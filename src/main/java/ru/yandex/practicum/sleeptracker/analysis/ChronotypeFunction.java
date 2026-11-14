package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.model.Chronotype;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChronotypeFunction implements SleepAnalysisFunction<Chronotype> {

    public static final String DESCRIPTION = "Хронотип пользователя";

    private static final LocalTime OWL_FALLS_ASLEEP_AFTER = LocalTime.of(23, 0);
    private static final LocalTime OWL_WAKES_UP_AFTER = LocalTime.of(9, 0);
    private static final LocalTime LARK_FALLS_ASLEEP_BEFORE = LocalTime.of(22, 0);
    private static final LocalTime LARK_WAKES_UP_BEFORE = LocalTime.of(7, 0);

    @Override
    public SleepAnalysisResult<Chronotype> apply(List<SleepingSession> sessions) {
        Map<Chronotype, Long> nightCountByChronotype = sessions.stream()
                .filter(Nights::coversAnyNight)
                .map(NightSleep::of)
                .collect(Collectors.toMap(NightSleep::night, Function.identity(), NightSleep::merge))
                .values()
                .stream()
                .collect(Collectors.groupingBy(ChronotypeFunction::chronotypeOf, Collectors.counting()));
        return new SleepAnalysisResult<>(DESCRIPTION, mostFrequent(nightCountByChronotype));
    }

    private static Chronotype chronotypeOf(NightSleep sleep) {
        LocalDate evening = sleep.night().minusDays(1);
        LocalDate morning = sleep.night();
        if (sleep.fallAsleep().isAfter(evening.atTime(OWL_FALLS_ASLEEP_AFTER))
                && sleep.wakeUp().isAfter(morning.atTime(OWL_WAKES_UP_AFTER))) {
            return Chronotype.OWL;
        }
        if (sleep.fallAsleep().isBefore(evening.atTime(LARK_FALLS_ASLEEP_BEFORE))
                && sleep.wakeUp().isBefore(morning.atTime(LARK_WAKES_UP_BEFORE))) {
            return Chronotype.LARK;
        }
        return Chronotype.PIGEON;
    }

    private static Chronotype mostFrequent(Map<Chronotype, Long> nightCountByChronotype) {
        long maxNightCount = nightCountByChronotype.values().stream()
                .max(Comparator.naturalOrder())
                .orElse(0L);
        List<Chronotype> leaders = nightCountByChronotype.entrySet().stream()
                .filter(entry -> entry.getValue() == maxNightCount)
                .map(Map.Entry::getKey)
                .toList();
        return leaders.size() == 1 ? leaders.getFirst() : Chronotype.PIGEON;
    }
}
