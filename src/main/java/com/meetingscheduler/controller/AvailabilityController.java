
package com.meetingscheduler.controller;

import com.meetingscheduler.auth.AuthUser;
import com.meetingscheduler.auth.AuthUserRepository;
import com.meetingscheduler.model.Availability;
import com.meetingscheduler.repository.AvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityRepository availabilityRepository;
    private final AuthUserRepository authUserRepository;

    @GetMapping
    public List<Availability> all() {
        return availabilityRepository.findAll();
    }

    @GetMapping("/user/{userId}/date/{date}")
    public List<Availability> byUserAndDate(
            @PathVariable Long userId,
            @PathVariable String date) {

        return availabilityRepository.findByUserIdAndAvailableDate(
                userId, LocalDate.parse(date));
    }

    @PostMapping
    public Availability create(@RequestBody Availability request) {

        if (request.getUser() == null || request.getUser().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please select a team member.");
        }

        Long userId = request.getUser().getId();

        AuthUser user = authUserRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Account ID " + userId + " does not exist in auth_users."));

        if (request.getAvailableDate() == null
                || request.getStartTime() == null
                || request.getEndTime() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Date, start time, and end time are required.");
        }

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "End time must be later than start time.");
        }

        Availability availability = Availability.builder()
                .user(user)
                .availableDate(request.getAvailableDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        return availabilityRepository.save(availability);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        availabilityRepository.deleteById(id);
    }
}
