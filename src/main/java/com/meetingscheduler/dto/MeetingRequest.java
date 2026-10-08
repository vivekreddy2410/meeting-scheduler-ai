package com.meetingscheduler.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record MeetingRequest(
        @NotBlank String title,
        String description,
        @NotNull LocalDate meetingDate,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime,
        @NotNull Long organizerId,
        List<Long> participantIds
) {}
