package com.example.badmintonbooking.controller;

import com.example.badmintonbooking.dto.pricingrequestdto;
import com.example.badmintonbooking.dto.pricingresponsedto;
import com.example.badmintonbooking.service.pricing.pricingservice;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
public class pricingcontroller {

    private final pricingservice pricingService;

    @PostMapping("/preview")
    public pricingresponsedto previewPrice(
            @RequestBody pricingrequestdto request
    ) {
        return pricingService.calculatePrice(request);
    }
}