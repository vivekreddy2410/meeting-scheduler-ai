
package com.meetingscheduler.service;

import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NaturalLanguageService {

    public ParsedRequest parse(String text) {
        String lower = text.toLowerCase(Locale.ENGLISH);

        int duration = 30;
        Matcher durationMatcher = Pattern.compile(
                "(\\d+)\\s*(minute|minutes|min|mins|hour|hours)"
        ).matcher(lower);

        if (durationMatcher.find()) {
            int n = Integer.parseInt(durationMatcher.group(1));
            duration = durationMatcher.group(2).startsWith("hour")
                    ? n * 60 : n;
        }

        String period = "any";

        if (lower.contains("morning")) {
            period = "morning";
        } else if (lower.contains("afternoon")) {
            period = "afternoon";
        } else if (lower.contains("evening")) {
            period = "evening";
        }

        LocalDate date = LocalDate.now();

        if (lower.contains("tomorrow")) {
            date = date.plusDays(1);
        } else if (lower.contains("next monday")) {
            date = nextDay(DayOfWeek.MONDAY);
        } else if (lower.contains("next tuesday")) {
            date = nextDay(DayOfWeek.TUESDAY);
        } else if (lower.contains("next wednesday")) {
            date = nextDay(DayOfWeek.WEDNESDAY);
        } else if (lower.contains("next thursday")) {
            date = nextDay(DayOfWeek.THURSDAY);
        } else if (lower.contains("next friday")) {
            date = nextDay(DayOfWeek.FRIDAY);
        }

        // Detect a specific requested time, such as 10 AM or 2:30 PM.
        LocalTime requestedTime = null;

        Matcher timeMatcher = Pattern.compile(
                "\\b(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)\\b"
        ).matcher(lower);

        if (timeMatcher.find()) {
            int hour = Integer.parseInt(timeMatcher.group(1));
            int minute = timeMatcher.group(2) == null
                    ? 0 : Integer.parseInt(timeMatcher.group(2));
            String meridiem = timeMatcher.group(3);

            if (hour >= 1 && hour <= 12 && minute < 60) {
                hour = hour % 12;

                if (meridiem.equals("pm")) {
                    hour += 12;
                }

                requestedTime = LocalTime.of(hour, minute);
            }
        }

        String title = "Team Meeting";

        Matcher titleMatcher = Pattern.compile(
                "(?:schedule|book|arrange)\\s+(?:a\\s+)?"
                        + "(?:\\d+\\s+)?(?:minute|minutes|min)?\\s*"
                        + "(.+?)(?:\\s+(?:next|tomorrow|today|on)\\b"
                        + "|\\s+with\\b|$)",
                Pattern.CASE_INSENSITIVE
        ).matcher(text);

        if (titleMatcher.find() && !titleMatcher.group(1).isBlank()) {
            title = titleMatcher.group(1).trim();
        }

        return new ParsedRequest(
                title, duration, period, date, requestedTime
        );
    }

    private LocalDate nextDay(DayOfWeek day) {
        LocalDate now = LocalDate.now();
        int days = (day.getValue() - now.getDayOfWeek().getValue() + 7) % 7;
        return now.plusDays(days == 0 ? 7 : days);
    }

    public record ParsedRequest(
            String title,
            int duration,
            String period,
            LocalDate date,
            LocalTime requestedTime
    ) {}
}
