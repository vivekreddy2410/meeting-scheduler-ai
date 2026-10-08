package com.meetingscheduler.service;

import com.meetingscheduler.dto.MeetingRequest;
import com.meetingscheduler.model.*;
import com.meetingscheduler.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;

    @Transactional
    public Meeting create(MeetingRequest request) {
        User organizer = userRepository.findById(request.organizerId())
                .orElseThrow(() -> new EntityNotFoundException("Organizer not found"));

        Meeting meeting = Meeting.builder()
                .title(request.title())
                .description(request.description())
                .meetingDate(request.meetingDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .status(MeetingStatus.SCHEDULED)
                .organizer(organizer)
                .build();

        meeting = meetingRepository.save(meeting);

        if (request.participantIds() != null) {
            for (Long userId : request.participantIds()) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("Participant not found: " + userId));
                participantRepository.save(Participant.builder()
                        .meeting(meeting)
                        .user(user)
                        .responseStatus(ParticipantStatus.INVITED)
                        .build());
            }
        }
        return meeting;
    }

    public List<Meeting> all() {
        return meetingRepository.findAll();
    }

    public Meeting get(Long id) {
        return meetingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meeting not found"));
    }

    @Transactional
    public Meeting update(Long id, MeetingRequest request) {
        Meeting meeting = get(id);
        User organizer = userRepository.findById(request.organizerId())
                .orElseThrow(() -> new EntityNotFoundException("Organizer not found"));
        meeting.setTitle(request.title());
        meeting.setDescription(request.description());
        meeting.setMeetingDate(request.meetingDate());
        meeting.setStartTime(request.startTime());
        meeting.setEndTime(request.endTime());
        meeting.setOrganizer(organizer);
        meeting.setStatus(MeetingStatus.RESCHEDULED);
        return meetingRepository.save(meeting);
    }

    @Transactional
    public void cancel(Long id) {
        Meeting meeting = get(id);
        meeting.setStatus(MeetingStatus.CANCELLED);
        meetingRepository.save(meeting);
    }

    public List<Participant> participants(Long meetingId) {
        return participantRepository.findByMeetingId(meetingId);
    }
}
