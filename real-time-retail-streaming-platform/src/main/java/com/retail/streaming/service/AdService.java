package com.retail.streaming.service;

import com.retail.streaming.exception.CampaignNotFoundException;
import com.retail.streaming.exception.InvalidCampaignIdException;
import com.retail.streaming.model.AdClicksResponse;
import com.retail.streaming.repository.AdCampaignMetricsRepository;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class AdService {

    private static final Pattern CAMPAIGN_ID_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{1,64}$");

    private final AdCampaignMetricsRepository repository;

    public AdService(AdCampaignMetricsRepository repository) {
        this.repository = repository;
    }

    public AdClicksResponse getClicks(String campaignId) {
        if (campaignId == null || !CAMPAIGN_ID_PATTERN.matcher(campaignId).matches()) {
            throw new InvalidCampaignIdException();
        }
        return repository.findById(campaignId)
                .map(metrics -> new AdClicksResponse(metrics.getCampaignId(), metrics.getClicks()))
                .orElseThrow(() -> new CampaignNotFoundException(campaignId));
    }
}
