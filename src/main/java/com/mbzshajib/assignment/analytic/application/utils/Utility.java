package com.mbzshajib.assignment.analytic.application.utils;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.mbzshajib.assignment.analytic.application.utils.Constants.Common.*;

public class Utility {
    public static String convertToKeyTime(Long timeInMilisecond) {
        if (timeInMilisecond == null) throw new IllegalArgumentException("timeInMilisecond can not be null");
        return convertToKeyTime(Instant.ofEpochMilli(timeInMilisecond));
    }

    public static String convertToKeyTime(Instant instant) {
        if (instant == null) throw new IllegalArgumentException("Instant can not be null");
        return DateTimeFormatter.ofPattern(KEY_DATE_FORMAT)
                .withZone(ZoneId.systemDefault())
                .format(instant);
    }

    public static long getTimeDiffInSeconds(Long from, Long to) {

        if (from == null || to == null) throw new IllegalArgumentException("input parameter from/to can not be null");
        return Duration.between(Instant.ofEpochMilli(from), Instant.ofEpochMilli(to)).getSeconds();
    }

    public static List<String> getAllSecondsInTimeWindow(Instant endTime, int windowTimeInSecond) {
        if (windowTimeInSecond <= 0) throw new IllegalArgumentException("window time can not be <=0");
        List<String> result = new ArrayList<>();
        IntStream.rangeClosed(1, windowTimeInSecond)
                .forEach(second -> result.add(Utility.convertToKeyTime(endTime.minus(second, ChronoUnit.SECONDS))));
        return result;
    }

    public static String formatStatisticStorageKey(Integer processId, String dataId, String timeId) {
        if (processId < 0) throw new IllegalArgumentException("negetive is not allowed in processId");
        if (dataId == null) throw new IllegalArgumentException("dataId can not be null");
        if (timeId == null) throw new IllegalArgumentException("timeId can not be null");
        return dataId + KEY_SEPARATOR + timeId + KEY_SEPARATOR + String.format(KEY_THREAD_ID_FORMAT, processId);
    }
}
