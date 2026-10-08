package com.meetingscheduler.dto;

import java.util.List;

public record AiScheduleResponse(
        String interpretedTitle,
        int durationMinutes,
        String preferredPeriod,
        String message,
        List<SlotSuggestion> suggestions
) {}
