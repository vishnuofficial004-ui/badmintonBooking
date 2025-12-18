package com.example.badmintonbooking.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
public class Court {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourtType type;

    @Column(nullable = false)
    private Double hourlyRate;

    @Column(nullable = false)
    private Boolean active;

    // No-arg constructor
    public Court() {}

    // All-arg constructor
    public Court(Long id, String name, CourtType type, Double hourlyRate, Boolean active) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.hourlyRate = hourlyRate;
        this.active = active;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public CourtType getType() { return type; }
    public void setType(CourtType type) { this.type = type; }

    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
