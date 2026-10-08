package com.meetingscheduler.service;

import com.meetingscheduler.model.Meeting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    public void sendInvitation(Meeting meeting, String recipientEmail) {
        // Demo mode: log instead of sending real mail.
        // Configure JavaMail properties and replace this method with JavaMailSender
        // when you want real email delivery.
        log.info("MEETING INVITATION -> {} | {} | {} {}-{}",
                recipientEmail, meeting.getTitle(), meeting.getMeetingDate(),
                meeting.getStartTime(), meeting.getEndTime());
    }
}
