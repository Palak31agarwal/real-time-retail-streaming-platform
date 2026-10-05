package com.retail.streaming.repository;

import com.retail.streaming.model.AdCampaignMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdCampaignMetricsRepository extends JpaRepository<AdCampaignMetrics, String> {
}