package com.gaspar.personalmetadata.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {

    public static String toFormattedDate(String timestamp) {
        Instant instant = Instant.ofEpochMilli(Long.parseLong(timestamp));
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(instant);
    }

}
