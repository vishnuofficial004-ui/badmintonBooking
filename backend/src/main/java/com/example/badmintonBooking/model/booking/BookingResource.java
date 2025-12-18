package com.example.badmintonbooking.model.booking;

import com.example.badmintonbooking.model.ResourceType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType resourceType; // COURT / EQUIPMENT / COACH

    @Column(nullable = false)
    private Long resourceId; // Court.id / Equipment.id / Coach.id

    private Integer quantity; // for equipment only

    @Column(nullable = false)
    private Double price;
}

