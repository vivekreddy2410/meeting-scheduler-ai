
package com.meetingscheduler.service;

import com.meetingscheduler.dto.SlotSuggestion;
import com.meetingscheduler.model.Availability;
import com.meetingscheduler.repository.AvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final AvailabilityRepository availabilityRepository;

    public List<SlotSuggestion> findSlots(
            LocalDate date,
            int duration,
            String period,
            List<Long> userIds) {

        if (date == null) {
            throw new IllegalArgumentException("Meeting date is required.");
        }

        if (duration < 5 || duration > 480) {
            throw new IllegalArgumentException(
                    "Meeting duration must be between 5 and 480 minutes."
            );
        }

        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "Select an organizer and at least one participant."
            );
        }

        // Remove duplicate IDs but keep the requested participants only.
        List<Long> selectedIds = userIds.stream()
                .filter(id -> id != null)
                .distinct()
                .toList();

        if (selectedIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "Select valid participant IDs."
            );
        }

        List<List<Availability>> calendars = new ArrayList<>();

        for (Long userId : selectedIds) {
            calendars.add(
                    availabilityRepository
                            .findByUserIdAndAvailableDate(userId, date)
            );
        }

        // Every selected participant must have availability for this date.
        if (calendars.stream().anyMatch(List::isEmpty)) {
            return List.of();
        }

        LocalTime windowStart;
        LocalTime windowEnd;

        String requestedPeriod = period == null
                ? "any"
                : period.trim().toLowerCase();

        switch (requestedPeriod) {
            case "morning" -> {
                windowStart = LocalTime.of(9, 0);
                windowEnd = LocalTime.NOON;
            }
            case "afternoon" -> {
                windowStart = LocalTime.NOON;
                windowEnd = LocalTime.of(17, 0);
            }
            case "evening" -> {
                windowStart = LocalTime.of(17, 0);
                windowEnd = LocalTime.of(20, 0);
            }
            default -> {
                windowStart = LocalTime.of(9, 0);
                windowEnd = LocalTime.of(18, 0);
            }
        }

        
        List<SlotSuggestion> result = new ArrayList<>();

        for (LocalTime start = windowStart;
             !start.plusMinutes(duration).isAfter(windowEnd);
             start = start.plusMinutes(30)) {

            final LocalTime slotStart = start;
            final LocalTime slotEnd = slotStart.plusMinutes(duration);

            boolean everyoneAvailable = calendars.stream().allMatch(calendar ->
                    calendar.stream().anyMatch(a ->
                            !slotStart.isBefore(a.getStartTime())
                                    && !slotEnd.isAfter(a.getEndTime())
                    )
            );

            if (everyoneAvailable) {
                result.add(new SlotSuggestion(
                        date,
                        slotStart,
                        slotEnd,
                        "All selected participants have availability covering this slot."
                ));
            }

            if (result.size() >= 5) {
                break;
            }
        }

        return result;
    }
}

