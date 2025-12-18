package com.example.badmintonbooking.repository;

import com.example.badmintonbooking.model.booking.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface bookingrepository extends JpaRepository<Booking, Long> {

    List<Booking> findByDate(LocalDate date);
}

