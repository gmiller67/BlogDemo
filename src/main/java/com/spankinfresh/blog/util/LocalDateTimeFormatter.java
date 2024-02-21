package com.spankinfresh.blog.util;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateTimeFormatter implements Formatter<LocalDateTime> {
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy, 'at' hh:mm a");

    @Override
    public LocalDateTime parse(String s, Locale locale)
            throws ParseException {
        return LocalDateTime.parse(s, formatter);
    }

    @Override
    public String print(LocalDateTime localDateTime, Locale locale) {
        return localDateTime.format(formatter);
    }
}