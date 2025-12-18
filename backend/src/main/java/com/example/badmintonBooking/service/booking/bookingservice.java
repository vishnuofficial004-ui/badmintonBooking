package com.example.badmintonbooking.service.booking;

import com.example.badmintonbooking.dto.bookingrequestdto;
import com.example.badmintonbooking.dto.pricingrequestdto;
import com.example.badmintonbooking.dto.pricingresponsedto;
import com.example.badmintonbooking.model.*;
import com.example.badmintonbooking.model.booking.*;
import com.example.badmintonbooking.repository.*;
import com.example.badmintonbooking.service.pricing.pricingservice;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class bookingservice {

    private final CourtRepository courtRepository;
    private final CoachRepository coachRepository;
    private final EquipmentRepository equipmentRepository;
    private final availabilityrepository availabilityRepository;
    private final bookingrepository bookingRepository;
    private final bookingresourcerepository bookingResourceRepository;
    private final pricingservice pricingService;

    @Transactional
    public Booking createBooking(bookingrequestdto request) {

        /* =======================
           1️⃣ VALIDATE COURT
        ======================== */
        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new RuntimeException("Court not found"));

        availability courtSlot = availabilityRepository
                .findByResourceTypeAndResourceIdAndDateAndAvailableTrue(
                        ResourceType.COURT,
                        court.getId(),
                        request.getDate()
                ).stream()
                .filter(a -> a.getStartTime().equals(request.getStartTime())
                        && a.getEndTime().equals(request.getEndTime()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Court not available"));

        /* =======================
           2️⃣ VALIDATE COACH (OPTIONAL)
        ======================== */
        Coach coach = null;
        availability coachSlot = null;

        if (request.getCoachId() != null) {
            coach = coachRepository.findById(request.getCoachId())
                    .orElseThrow(() -> new RuntimeException("Coach not found"));

            coachSlot = availabilityRepository
                    .findByResourceTypeAndResourceIdAndDateAndAvailableTrue(
                            ResourceType.COACH,
                            coach.getId(),
                            request.getDate()
                    ).stream()
                    .filter(a -> a.getStartTime().equals(request.getStartTime())
                            && a.getEndTime().equals(request.getEndTime()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Coach not available"));
        }

        /* =======================
           3️⃣ VALIDATE EQUIPMENT
        ======================== */
        if (request.getEquipment() != null) {
            for (Map.Entry<Long, Integer> entry : request.getEquipment().entrySet()) {
                Equipment equipment = equipmentRepository.findById(entry.getKey())
                        .orElseThrow(() -> new RuntimeException("Equipment not found"));

                if (entry.getValue() > equipment.getTotalQuantity()) {
                    throw new RuntimeException(
                            "Not enough equipment available: " + equipment.getName()
                    );
                }
            }
        }

        /* =======================
           4️⃣ CALCULATE PRICE (PHASE 6)
        ======================== */
        pricingrequestdto pricingRequest = pricingrequestdto.builder()
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .courtId(request.getCourtId())
                .coachId(request.getCoachId())
                .equipment(request.getEquipment())
                .build();

        pricingresponsedto pricingResponse =
                pricingService.calculatePrice(pricingRequest);

        /* =======================
           5️⃣ CREATE BOOKING
        ======================== */
        Booking booking = Booking.builder()
                .userName(request.getUserName())
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(BookingStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .totalPrice(pricingResponse.getTotalPrice())
                .build();

        bookingRepository.save(booking);

        List<BookingResource> resources = new ArrayList<>();

        /* =======================
           6️⃣ RESERVE COURT
        ======================== */
        courtSlot.setAvailable(false);
        availabilityRepository.save(courtSlot);

        resources.add(BookingResource.builder()
                .booking(booking)
                .resourceType(ResourceType.COURT)
                .resourceId(court.getId())
                .price(pricingResponse.getBaseCourtPrice())
                .build());

        /* =======================
           7️⃣ RESERVE COACH
        ======================== */
        if (coach != null) {
            coachSlot.setAvailable(false);
            availabilityRepository.save(coachSlot);

            resources.add(BookingResource.builder()
                    .booking(booking)
                    .resourceType(ResourceType.COACH)
                    .resourceId(coach.getId())
                    .price(coach.getPricePerSlot())
                    .build());
        }

        /* =======================
           8️⃣ RESERVE EQUIPMENT
        ======================== */
        if (request.getEquipment() != null) {
            for (Map.Entry<Long, Integer> entry : request.getEquipment().entrySet()) {
                Equipment equipment = equipmentRepository.findById(entry.getKey()).get();

                resources.add(BookingResource.builder()
                        .booking(booking)
                        .resourceType(ResourceType.EQUIPMENT)
                        .resourceId(equipment.getId())
                        .quantity(entry.getValue())
                        .price(equipment.getPricePerSlot() * entry.getValue())
                        .build());
            }
        }

        bookingResourceRepository.saveAll(resources);
        booking.setResources(resources);

        return booking;
    }
}

