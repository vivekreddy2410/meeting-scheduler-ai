package com.meetingscheduler.repository;

import com.meetingscheduler.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByUserIdAndAvailableDate(Long userId, LocalDate date);
}
