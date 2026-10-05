package com.retail.streaming.service;

import com.retail.streaming.model.AdClicksResponse;
import com.retail.streaming.repository.AdCampaignMetricsRepository;
import org.springframework.stereotype.Service;

@Service
public class AdService {

    private final AdCampaignMetricsRepository repository;

    public AdService(AdCampaignMetricsRepository repository) {
        this.repository = repository;
    }

    public AdClicksResponse getClicks(String campaignId) {
        long clicks = repository.findById(campaignId)
                .map(metrics -> metrics.getClicks())
                .orElse(0L);
        return new AdClicksResponse(campaignId, clicks);
    }
}
