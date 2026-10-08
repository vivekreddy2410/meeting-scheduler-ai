package com.meetingscheduler.controller;

import com.meetingscheduler.dto.MeetingRequest;
import com.meetingscheduler.model.Meeting;
import com.meetingscheduler.model.Participant;
import com.meetingscheduler.service.MeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
@CrossOrigin
public class MeetingController {
    private final MeetingService service;

    @GetMapping
    public List<Meeting> all() { return service.all(); }

    @GetMapping("/{id}")
    public Meeting one(@PathVariable Long id) { return service.get(id); }

    @GetMapping("/{id}/participants")
    public List<Participant> participants(@PathVariable Long id) {
        return service.participants(id);
    }

    @PostMapping
    public Meeting create(@Valid @RequestBody MeetingRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public Meeting update(@PathVariable Long id, @Valid @RequestBody MeetingRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.cancel(id); }
}
