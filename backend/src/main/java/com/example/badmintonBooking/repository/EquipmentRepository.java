package com.example.badmintonbooking.repository;

import com.example.badmintonbooking.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
}
