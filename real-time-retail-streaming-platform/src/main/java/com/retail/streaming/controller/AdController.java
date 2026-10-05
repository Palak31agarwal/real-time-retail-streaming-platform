package com.retail.streaming.controller;

import com.retail.streaming.model.AdClicksResponse;
import com.retail.streaming.service.AdService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ad")
public class AdController {

    private final AdService adService;

    public AdController(AdService adService) {
        this.adService = adService;
    }

    @GetMapping("/{campaignId}/clicks")
    public AdClicksResponse getClicks(@PathVariable String campaignId) {
        return adService.getClicks(campaignId);
    }
}
