package com.example.badmintonbooking.config;

import com.example.badmintonbooking.model.Court;
import com.example.badmintonbooking.model.Coach;
import com.example.badmintonbooking.model.booking.availability;
import com.example.badmintonbooking.model.ResourceType;
import com.example.badmintonbooking.repository.CourtRepository;
import com.example.badmintonbooking.repository.CoachRepository;
import com.example.badmintonbooking.repository.availabilityrepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class availabilitydataloader {

    private final CourtRepository courtRepository;
    private final CoachRepository coachRepository;
    private final availabilityrepository availabilityRepository;

    @Bean
    CommandLineRunner loadAvailability() {
        return args -> {
            List<availability> slots = new ArrayList<>();
            LocalDate today = LocalDate.now();
            int daysToGenerate = 7; // next 7 days

            LocalTime startTime = LocalTime.of(6, 0); // 6 AM
            LocalTime endTime = LocalTime.of(22, 0); // 10 PM
            int slotDurationMinutes = 60; // 1-hour slots

            // Generate slots for courts
            List<Court> courts = courtRepository.findAll();
            for (int d = 0; d < daysToGenerate; d++) {
                LocalDate date = today.plusDays(d);
                for (Court court : courts) {
                    LocalTime time = startTime;
                    while (time.isBefore(endTime)) {
                        slots.add(availability.builder()
                                .resourceType(ResourceType.COURT)
                                .resourceId(court.getId())
                                .date(date)
                                .startTime(time)
                                .endTime(time.plusMinutes(slotDurationMinutes))
                                .available(true)
                                .build());
                        time = time.plusMinutes(slotDurationMinutes);
                    }
                }
            }

            // Generate slots for coaches
            List<Coach> coaches = coachRepository.findAll();
            for (int d = 0; d < daysToGenerate; d++) {
                LocalDate date = today.plusDays(d);
                for (Coach coach : coaches) {
                    LocalTime time = startTime;
                    while (time.isBefore(endTime)) {
                        slots.add(availability.builder()
                                .resourceType(ResourceType.COACH)
                                .resourceId(coach.getId())
                                .date(date)
                                .startTime(time)
                                .endTime(time.plusMinutes(slotDurationMinutes))
                                .available(true)
                                .build());
                        time = time.plusMinutes(slotDurationMinutes);
                    }
                }
            }

            availabilityRepository.saveAll(slots);
        };
    }
}
