package com.example.badmintonbooking.controller;
import com.example.badmintonbooking.dto.bookingrequestdto;
import com.example.badmintonbooking.model.booking.Booking;
import com.example.badmintonbooking.repository.bookingrepository;
import com.example.badmintonbooking.service.booking.bookingservice;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class bookingcontroller {

    private final bookingservice bookingService;
    private final bookingrepository bookingRepository;

    @PostMapping
    public ResponseEntity<Booking> createBooking(
            @RequestBody bookingrequestdto request
    ) {
        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<Booking>> getBookingHistory(
            @RequestParam String userName
    ) {
        return ResponseEntity.ok(
                bookingRepository.findByUserName(userName)
        );
    }
}

