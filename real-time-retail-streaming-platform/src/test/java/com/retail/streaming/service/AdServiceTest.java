package com.retail.streaming.service;

import com.retail.streaming.model.AdCampaignMetrics;
import com.retail.streaming.repository.AdCampaignMetricsRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdServiceTest {

    @Test
    void shouldReturnClicksForCampaign() {
        AdCampaignMetricsRepository repository = mock(AdCampaignMetricsRepository.class);
        when(repository.findById("C123")).thenReturn(Optional.of(new AdCampaignMetrics("C123", 50234)));
        AdService service = new AdService(repository);

        var response = service.getClicks("C123");

        assertEquals("C123", response.campaignId());
        assertEquals(50234, response.clicks());
    }

    @Test
    void shouldReturnZeroWhenCampaignDoesNotExist() {
        AdCampaignMetricsRepository repository = mock(AdCampaignMetricsRepository.class);
        when(repository.findById("missing")).thenReturn(Optional.empty());
        AdService service = new AdService(repository);

        var response = service.getClicks("missing");

        assertEquals("missing", response.campaignId());
        assertEquals(0, response.clicks());
    }
}
