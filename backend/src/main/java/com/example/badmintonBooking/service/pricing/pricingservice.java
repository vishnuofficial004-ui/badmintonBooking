package com.example.badmintonbooking.service.pricing;
import com.example.badmintonbooking.dto.pricingrequestdto;
import com.example.badmintonbooking.dto.pricingresponsedto;
import com.example.badmintonbooking.model.*;
import com.example.badmintonbooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class pricingservice {

    private final CourtRepository courtRepository;
    private final CoachRepository coachRepository;
    private final EquipmentRepository equipmentRepository;
    private final PricingRuleRepository pricingRuleRepository;

    public pricingresponsedto calculatePrice(pricingrequestdto request) {

        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new RuntimeException("Court not found"));

        double baseCourtPrice = court.getHourlyRate();
        List<String> appliedRules = new ArrayList<>();

        List<PricingRule> activeRules = pricingRuleRepository.findByActiveTrue();

        for (PricingRule rule : activeRules) {
            if (isRuleApplicable(rule, court, request)) {
                baseCourtPrice *= rule.getMultiplier();
                appliedRules.add(rule.getName());
            }
        }

        // Equipment price
        double equipmentPrice = 0.0;
        if (request.getEquipment() != null) {
            for (Map.Entry<Long, Integer> entry : request.getEquipment().entrySet()) {
                Equipment equipment = equipmentRepository.findById(entry.getKey())
                        .orElseThrow(() -> new RuntimeException("Equipment not found"));
                equipmentPrice += equipment.getPricePerSlot() * entry.getValue();
            }
        }

        // Coach price
        double coachPrice = 0.0;
        if (request.getCoachId() != null) {
            Coach coach = coachRepository.findById(request.getCoachId())
                    .orElseThrow(() -> new RuntimeException("Coach not found"));
            coachPrice = coach.getPricePerSlot();
        }

        double totalPrice = baseCourtPrice + equipmentPrice + coachPrice;

        return pricingresponsedto.builder()
                .baseCourtPrice(baseCourtPrice)
                .appliedRules(appliedRules)
                .equipmentPrice(equipmentPrice)
                .coachPrice(coachPrice)
                .totalPrice(totalPrice)
                .build();
    }

    private boolean isRuleApplicable(PricingRule rule, Court court, pricingrequestdto request) {

        // Indoor rule
        if (rule.getRuleType() == PricingRuleType.INDOOR &&
                court.getType() != CourtType.INDOOR) {
            return false;
        }

        // Day rule
        if (rule.getDayType() != null && rule.getDayType() != DayType.ANY) {
            DayOfWeek day = request.getDate().getDayOfWeek();
            boolean isWeekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;

            if (rule.getDayType() == DayType.WEEKEND && !isWeekend) {
                return false;
            }
            if (rule.getDayType() == DayType.WEEKDAY && isWeekend) {
                return false;
            }
        }

        if (rule.getStartTime() != null && rule.getEndTime() != null) {
            if (request.getStartTime().isBefore(rule.getStartTime())
                    || request.getEndTime().isAfter(rule.getEndTime())) {
                return false;
            }
        }

        return true;
    }
}
