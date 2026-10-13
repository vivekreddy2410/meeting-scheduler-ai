
package com.meetingscheduler.repository;

import com.meetingscheduler.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    List<Availability> findByUserIdAndAvailableDate(Long userId, LocalDate date);

    List<Availability> findAllByOrderByAvailableDateAscStartTimeAsc();

    @Modifying
    @Query("""
        DELETE FROM Availability a
        WHERE a.availableDate < :today
           OR (a.availableDate = :today AND a.endTime < :currentTime)
        """)
    int deleteExpiredAvailability(
            @Param("today") LocalDate today,
            @Param("currentTime") LocalTime currentTime
    );
}
