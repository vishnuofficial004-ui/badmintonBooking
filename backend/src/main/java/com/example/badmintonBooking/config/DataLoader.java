package com.example.badmintonbooking.config;

import com.example.badmintonbooking.model.*;
import com.example.badmintonbooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    private final CourtRepository courtRepository;
    private final EquipmentRepository equipmentRepository;
    private final CoachRepository coachRepository;
    private final PricingRuleRepository pricingRuleRepository;

    @Bean
    CommandLineRunner loadInitialData() {
        return args -> {

            // Courts
            courtRepository.saveAll(List.of(
                    Court.builder().name("Court 1").type(CourtType.INDOOR).hourlyRate(500.0).active(true).build(),
                    Court.builder().name("Court 2").type(CourtType.INDOOR).hourlyRate(500.0).active(true).build(),
                    Court.builder().name("Court 3").type(CourtType.OUTDOOR).hourlyRate(300.0).active(true).build(),
                    Court.builder().name("Court 4").type(CourtType.OUTDOOR).hourlyRate(300.0).active(true).build()
            ));

            // Equipment
            equipmentRepository.saveAll(List.of(
                    Equipment.builder().name("Racket").totalQuantity(10).pricePerSlot(50.0).active(true).build(),
                    Equipment.builder().name("Shoes").totalQuantity(5).pricePerSlot(80.0).active(true).build()
            ));

            // Coaches
            coachRepository.saveAll(List.of(
                    Coach.builder().name("Coach A").pricePerSlot(200.0).active(true).build(),
                    Coach.builder().name("Coach B").pricePerSlot(200.0).active(true).build(),
                    Coach.builder().name("Coach C").pricePerSlot(250.0).active(true).build()
            ));

            // Pricing Rules
            pricingRuleRepository.saveAll(List.of(
                    PricingRule.builder()
                            .name("Peak Hour Pricing")
                            .ruleType(PricingRuleType.PEAK_HOUR)
                            .multiplier(1.5)
                            .startTime(LocalTime.of(18, 0))
                            .endTime(LocalTime.of(21, 0))
                            .dayType(DayType.ANY)
                            .active(true)
                            .build(),

                    PricingRule.builder()
                            .name("Weekend Pricing")
                            .ruleType(PricingRuleType.WEEKEND)
                            .multiplier(1.2)
                            .dayType(DayType.WEEKEND)
                            .active(true)
                            .build(),

                    PricingRule.builder()
                            .name("Indoor Court Premium")
                            .ruleType(PricingRuleType.INDOOR)
                            .multiplier(1.3)
                            .dayType(DayType.ANY)
                            .active(true)
                            .build()
            ));
        };
    }
}

