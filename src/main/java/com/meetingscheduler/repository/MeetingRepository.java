package com.meetingscheduler.repository;

import com.meetingscheduler.model.Meeting;
import com.meetingscheduler.model.MeetingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByMeetingDateGreaterThanEqualAndStatusOrderByMeetingDateAscStartTimeAsc(
            LocalDate date, MeetingStatus status);
}
