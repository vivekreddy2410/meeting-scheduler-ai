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

        if (userIds == null || userIds.isEmpty()) {
            userIds = availabilityRepository.findAll()
                    .stream()
                    .map(a -> a.getUser().getId())
                    .distinct()
                    .toList();
        }

        List<List<Availability>> calendars = new ArrayList<>();

        for (Long userId : userIds) {
            calendars.add(
                    availabilityRepository
                            .findByUserIdAndAvailableDate(userId, date)
            );
        }

        List<SlotSuggestion> result = new ArrayList<>();

        if (calendars.isEmpty()) {
            return result;
        }

        LocalTime windowStart;

        switch (period) {
            case "morning":
                windowStart = LocalTime.of(9, 0);
                break;

            case "afternoon":
                windowStart = LocalTime.of(13, 0);
                break;

            case "evening":
                windowStart = LocalTime.of(17, 0);
                break;

            default:
                windowStart = LocalTime.of(9, 0);
        }

        LocalTime windowEnd;

        switch (period) {
            case "morning":
                windowEnd = LocalTime.of(12, 0);
                break;

            case "afternoon":
                windowEnd = LocalTime.of(17, 0);
                break;

            case "evening":
                windowEnd = LocalTime.of(20, 0);
                break;

            default:
                windowEnd = LocalTime.of(20, 0);
        }

        for (
                LocalTime currentStart = windowStart;
                !currentStart.plusMinutes(duration).isAfter(windowEnd);
                currentStart = currentStart.plusMinutes(30)
        ) {

            final LocalTime slotStart = currentStart;
            final LocalTime slotEnd = currentStart.plusMinutes(duration);

            boolean everyoneAvailable = true;

            for (List<Availability> calendar : calendars) {

                boolean available = calendar.stream().anyMatch(a ->
                        !slotStart.isBefore(a.getStartTime())
                                && !slotEnd.isAfter(a.getEndTime())
                );

                if (!available) {
                    everyoneAvailable = false;
                    break;
                }
            }

            if (everyoneAvailable) {

                result.add(
                        new SlotSuggestion(
                                date,
                                slotStart,
                                slotEnd,
                                "All requested participants are available in this time window."
                        )
                );
            }

            if (result.size() == 5) {
                break;
            }
        }

        return result;
    }
}
