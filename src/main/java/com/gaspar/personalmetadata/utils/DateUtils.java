package com.gaspar.personalmetadata.utils;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static String toFormattedDate(String timestamp) {
        Instant instant = Instant.ofEpochMilli(Long.parseLong(timestamp));
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(instant);
    }

}
