package com.retail.streaming.exception;

public class CampaignNotFoundException extends RuntimeException {

    public CampaignNotFoundException(String campaignId) {
        super("Campaign not found: " + campaignId);
    }
}
