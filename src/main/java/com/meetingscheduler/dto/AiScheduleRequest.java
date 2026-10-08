package com.meetingscheduler.dto;

import jakarta.validation.constraints.NotBlank;

public record AiScheduleRequest(@NotBlank String request, Long organizerId) {}
