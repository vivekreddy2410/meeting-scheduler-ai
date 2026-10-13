
package com.meetingscheduler.service;

import com.meetingscheduler.repository.AvailabilityRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class AvailabilityCleanupService {

    private final AvailabilityRepository availabilityRepository;

    public AvailabilityCleanupService(
            AvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void deleteExpiredAvailability() {
        int deleted = availabilityRepository.deleteExpiredAvailability(
                LocalDate.now(),
                LocalTime.now()
        );

        if (deleted > 0) {
            System.out.println(
                    "Deleted " + deleted + " expired availability records."
            );
        }
    }
}
