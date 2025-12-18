package com.example.badmintonbooking.repository;

import com.example.badmintonbooking.model.Court;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourtRepository extends JpaRepository<Court, Long> {
}
