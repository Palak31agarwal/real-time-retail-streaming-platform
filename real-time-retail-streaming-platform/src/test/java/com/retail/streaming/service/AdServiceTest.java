package com.retail.streaming.service;

import com.retail.streaming.exception.CampaignNotFoundException;
import com.retail.streaming.exception.InvalidCampaignIdException;
import com.retail.streaming.model.AdCampaignMetrics;
import com.retail.streaming.repository.AdCampaignMetricsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AdServiceTest {

    private AdCampaignMetricsRepository repository;
    private AdService service;

    @BeforeEach
    void setUp() {
        repository = mock(AdCampaignMetricsRepository.class);
        service = new AdService(repository);
    }

    @Test
    void shouldReturnClicksForCampaign() {
        when(repository.findById("C123")).thenReturn(Optional.of(new AdCampaignMetrics("C123", 50234)));

        var response = service.getClicks("C123");

        assertEquals("C123", response.campaignId());
        assertEquals(50234, response.clicks());
    }

    @Test
    void shouldReturnZeroClicksWhenCampaignExistsWithNoClicks() {
        when(repository.findById("C789")).thenReturn(Optional.of(new AdCampaignMetrics("C789", 0)));

        var response = service.getClicks("C789");

        assertEquals("C789", response.campaignId());
        assertEquals(0, response.clicks());
    }

    @Test
    void shouldThrowNotFoundWhenCampaignDoesNotExist() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(CampaignNotFoundException.class, () -> service.getClicks("missing"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "C 123", "C123;DROP TABLE", "../etc/passwd", "C123\n", "campaign!"})
    void shouldRejectInvalidCampaignIdWithoutQueryingRepository(String campaignId) {
        assertThrows(InvalidCampaignIdException.class, () -> service.getClicks(campaignId));

        verifyNoInteractions(repository);
    }

    @Test
    void shouldRejectCampaignIdLongerThanMaxLength() {
        String tooLong = "A".repeat(65);

        assertThrows(InvalidCampaignIdException.class, () -> service.getClicks(tooLong));
    }

    @Test
    void shouldAcceptCampaignIdAtMaxLength() {
        String maxLength = "A".repeat(64);
        when(repository.findById(maxLength)).thenReturn(Optional.of(new AdCampaignMetrics(maxLength, 1)));

        assertEquals(1, service.getClicks(maxLength).clicks());
    }

    @Test
    void shouldPropagateDataAccessFailures() {
        when(repository.findById("C123")).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThrows(DataAccessResourceFailureException.class, () -> service.getClicks("C123"));
    }
}
