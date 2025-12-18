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
public class pricingrequestdto {

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    private Long courtId;
    private Long coachId; // optional

    // equipmentId -> quantity
    private Map<Long, Integer> equipment;
}

