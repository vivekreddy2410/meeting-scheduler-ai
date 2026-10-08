package com.meetingscheduler.controller;

import com.meetingscheduler.dto.*;
import com.meetingscheduler.service.NaturalLanguageService;
import com.meetingscheduler.service.SchedulerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin
public class AiController {
    private final NaturalLanguageService nlp;
    private final SchedulerService scheduler;

    @PostMapping("/schedule")
    public AiScheduleResponse schedule(@Valid @RequestBody AiScheduleRequest request) {
        NaturalLanguageService.ParsedRequest parsed = nlp.parse(request.request());

        var suggestions = scheduler.findSlots(
                parsed.date(),
                parsed.duration(),
                parsed.period(),
                request.organizerId() == null ? null : java.util.List.of(request.organizerId())
        );

        String message = suggestions.isEmpty()
                ? "No common slot was found. Try another date or time period."
                : "Common availability found. Select a slot to create the meeting.";

        return new AiScheduleResponse(
                parsed.title(),
                parsed.duration(),
                parsed.period(),
                message,
                suggestions
        );
    }
}
