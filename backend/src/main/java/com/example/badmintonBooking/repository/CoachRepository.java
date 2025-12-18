package com.example.badmintonbooking.repository;

import com.example.badmintonbooking.model.Coach;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoachRepository extends JpaRepository<Coach, Long> {
}
