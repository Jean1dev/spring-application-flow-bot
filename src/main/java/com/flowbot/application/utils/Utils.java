package com.flowbot.application.utils;

import java.time.Duration;
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

    public static String calculateElapsedTime(final LocalDateTime date) {
        var now = LocalDateTime.now();
        var duration = Duration.between(date, now);
        long years = duration.toDays() / 365;
        long months = (duration.toDays() / 30) % 12;
        long days = duration.toDays() % 30;
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;

        if (years > 0) {
            return years + " ano(s) atrás";
        } else if (months > 0) {
            return months + " mês(es) atrás";
        } else if (days > 0) {
            return days + " dia(s) atrás";
        } else if (hours > 0) {
            return hours + " hora(s) e " + minutes + " minuto(s) atrás";
        } else if (minutes > 0) {
            return minutes + " minuto(s) atrás";
        }

        return "Agora";
    }

    public static String generateWhatsappId(int length) {
        StringBuilder sb = new StringBuilder();
        String characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }
}
