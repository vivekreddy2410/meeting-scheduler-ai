package com.meetingscheduler.controller;

import com.meetingscheduler.model.Availability;
import com.meetingscheduler.repository.AvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
@CrossOrigin
public class AvailabilityController {
    private final AvailabilityRepository repository;

    @GetMapping
    public List<Availability> all() { return repository.findAll(); }

    @GetMapping("/user/{userId}/date/{date}")
    public List<Availability> byUserAndDate(@PathVariable Long userId, @PathVariable String date) {
        return repository.findByUserIdAndAvailableDate(userId, LocalDate.parse(date));
    }

    @PostMapping
    public Availability create(@RequestBody Availability availability) {
        return repository.save(availability);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repository.deleteById(id); }
}
