package com.example.badmintonbooking.repository;

import com.example.badmintonbooking.model.booking.availability;
import com.example.badmintonbooking.model.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface availabilityrepository extends JpaRepository<availability, Long> {

    List<availability> findByResourceTypeAndResourceIdAndDateAndAvailableTrue(
            ResourceType resourceType, Long resourceId, LocalDate date);

    List<availability> findByResourceTypeAndDateAndAvailableTrue(
            ResourceType resourceType, LocalDate date);
}

