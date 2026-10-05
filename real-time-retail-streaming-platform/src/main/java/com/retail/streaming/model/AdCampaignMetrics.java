package com.retail.streaming.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ad_campaign_metrics")
public class AdCampaignMetrics {

    @Id
    private String campaignId;

    private long clicks;

    protected AdCampaignMetrics() {
    }

    public AdCampaignMetrics(String campaignId, long clicks) {
        this.campaignId = campaignId;
        this.clicks = clicks;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public long getClicks() {
        return clicks;
    }
}