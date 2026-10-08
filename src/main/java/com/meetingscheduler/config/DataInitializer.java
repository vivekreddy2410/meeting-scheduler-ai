package com.meetingscheduler.config;

import com.meetingscheduler.model.*;
import com.meetingscheduler.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    @Bean
    CommandLineRunner seed(UserRepository users, AvailabilityRepository availability) {
        return args -> {
            if (users.count() > 0) return;

            User vivek = users.save(User.builder()
                    .name("Vivek")
                    .email("vivek@example.com")
                    .timezone("Asia/Kolkata")
                    .workingStart("09:00")
                    .workingEnd("18:00")
                    .build());

            User rahul = users.save(User.builder()
                    .name("Rahul")
                    .email("rahul@example.com")
                    .timezone("Asia/Kolkata")
                    .workingStart("09:00")
                    .workingEnd("18:00")
                    .build());

            User priya = users.save(User.builder()
                    .name("Priya")
                    .email("priya@example.com")
                    .timezone("Asia/Kolkata")
                    .workingStart("09:00")
                    .workingEnd("18:00")
                    .build());

            LocalDate tomorrow = LocalDate.now().plusDays(1);
            for (User u : new User[]{vivek, rahul, priya}) {
                availability.save(Availability.builder()
                        .user(u)
                        .availableDate(tomorrow)
                        .startTime(LocalTime.of(9, 0))
                        .endTime(LocalTime.of(18, 0))
                        .build());
            }
        };
    }
}
