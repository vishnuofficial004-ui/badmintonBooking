package com.example.badmintonbooking.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class bookingrequestdto {

    private String userName;

    private LocalDate date;

    private LocalTime startTime;
    private LocalTime endTime;

    private Long courtId;
    private Map<Long, Integer> equipment;

    private Long coachId; 
}
