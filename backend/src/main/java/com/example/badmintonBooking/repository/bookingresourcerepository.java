package com.example.badmintonbooking.repository;

import com.example.badmintonbooking.model.booking.BookingResource;
import com.example.badmintonbooking.model.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface bookingresourcerepository extends JpaRepository<BookingResource, Long> {

    List<BookingResource> findByResourceTypeAndResourceId(ResourceType resourceType, Long resourceId);
}

