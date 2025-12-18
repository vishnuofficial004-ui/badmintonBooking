package com.example.badmintonbooking.service.booking;

import com.example.badmintonbooking.dto.bookingrequestdto;
import com.example.badmintonbooking.model.*;
import com.example.badmintonbooking.model.booking.*;
import com.example.badmintonbooking.repository.CoachRepository;
import com.example.badmintonbooking.repository.CourtRepository;
import com.example.badmintonbooking.repository.EquipmentRepository;
import com.example.badmintonbooking.repository.availabilityrepository;
import com.example.badmintonbooking.repository.bookingrepository;
import com.example.badmintonbooking.repository.bookingresourcerepository;

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

    @Transactional
    public Booking createBooking(bookingrequestdto request) {

        // 1️⃣ Validate court
        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new RuntimeException("Court not found"));

        // 2️⃣ Check court availability
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

        // 3️⃣ Check coach availability (optional)
        availability coachSlot = null;
        Coach coach = null;

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

        // 4️⃣ Check equipment availability
        if (request.getEquipment() != null) {
            for (Map.Entry<Long, Integer> entry : request.getEquipment().entrySet()) {
                Equipment equipment = equipmentRepository.findById(entry.getKey())
                        .orElseThrow(() -> new RuntimeException("Equipment not found"));

                if (entry.getValue() > equipment.getTotalQuantity()) {
                    throw new RuntimeException("Not enough equipment available: " + equipment.getName());
                }
            }
        }

        // 5️⃣ Create booking
        Booking booking = Booking.builder()
                .userName(request.getUserName())
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(BookingStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .totalPrice(0.0) // will be calculated later
                .build();

        bookingRepository.save(booking);

        List<BookingResource> resources = new ArrayList<>();

        // 6️⃣ Reserve court
        courtSlot.setAvailable(false);
        availabilityRepository.save(courtSlot);

        resources.add(BookingResource.builder()
                .booking(booking)
                .resourceType(ResourceType.COURT)
                .resourceId(court.getId())
                .price(court.getHourlyRate())
                .build());

        // 7️⃣ Reserve coach
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

        // 8️⃣ Reserve equipment
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
