package com.meetingscheduler.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record SlotSuggestion(LocalDate date, LocalTime startTime, LocalTime endTime, String reason) {}
