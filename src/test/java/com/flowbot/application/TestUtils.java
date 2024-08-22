package com.flowbot.application;

import java.time.LocalDateTime;

public final class TestUtils {

    public static boolean compareIgnoringSecondsAndMillis(LocalDateTime dt1, LocalDateTime dt2) {
        return dt1.getYear() == dt2.getYear() &&
                dt1.getMonth() == dt2.getMonth() &&
                dt1.getDayOfMonth() == dt2.getDayOfMonth() &&
                dt1.getHour() == dt2.getHour() &&
                dt1.getMinute() == dt2.getMinute();
    }
}
