package com.example.badmintonbooking.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class pricingresponsedto {

    private Double baseCourtPrice;
    private List<String> appliedRules;
    private Double equipmentPrice;
    private Double coachPrice;
    private Double totalPrice;
}
