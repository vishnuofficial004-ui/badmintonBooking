package com.example.badmintonbooking.controller;

import com.example.badmintonbooking.dto.bookingrequestdto;
import com.example.badmintonbooking.model.booking.Booking;
import com.example.badmintonbooking.service.booking.bookingservice;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class bookingcontroller {

    private final bookingservice bookingService;

    @PostMapping
    public ResponseEntity<Booking> createBooking(
            @RequestBody bookingrequestdto request
    ) {
        Booking booking = bookingService.createBooking(request);
        return new ResponseEntity<>(booking, HttpStatus.CREATED);
    }
}
