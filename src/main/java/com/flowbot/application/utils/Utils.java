package com.flowbot.application.utils;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Utils {

    public static LocalDateTime nullOrToday(LocalDateTime date) {
        return date == null ? LocalDateTime.now() : date;
    }

    public static Object nullOrValue(Object comparator, Object orElse) {
        if (Objects.isNull(comparator))
            return orElse;

        return comparator;
    }
}
