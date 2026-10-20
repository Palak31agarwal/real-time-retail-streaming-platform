package com.retail.streaming.controller;

import com.retail.streaming.exception.CampaignNotFoundException;
import com.retail.streaming.exception.GlobalExceptionHandler;
import com.retail.streaming.exception.InvalidCampaignIdException;
import com.retail.streaming.model.AdClicksResponse;
import com.retail.streaming.service.AdService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdController.class)
@Import(GlobalExceptionHandler.class)
class AdControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdService adService;

    @Test
    void shouldReturn200WithClicks() throws Exception {
        when(adService.getClicks("C123")).thenReturn(new AdClicksResponse("C123", 50234));

        mockMvc.perform(get("/ad/C123/clicks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campaignId").value("C123"))
                .andExpect(jsonPath("$.clicks").value(50234));
    }

    @Test
    void shouldReturn404WhenCampaignNotFound() throws Exception {
        when(adService.getClicks("missing")).thenThrow(new CampaignNotFoundException("missing"));

        mockMvc.perform(get("/ad/missing/clicks"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Campaign not found: missing"));
    }

    @Test
    void shouldReturn400WhenCampaignIdInvalid() throws Exception {
        when(adService.getClicks("bad!id")).thenThrow(new InvalidCampaignIdException());

        mockMvc.perform(get("/ad/bad!id/clicks"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void shouldReturn503WithoutLeakingDetailsWhenDatabaseFails() throws Exception {
        when(adService.getClicks("C123")).thenThrow(new DataAccessResourceFailureException("jdbc:h2:file:./data/retaildb"));

        mockMvc.perform(get("/ad/C123/clicks"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("Service temporarily unavailable"));
    }

    @Test
    void shouldReturn500WithGenericMessageForUnexpectedErrors() throws Exception {
        when(adService.getClicks("C123")).thenThrow(new IllegalStateException("secret internal detail"));

        mockMvc.perform(get("/ad/C123/clicks"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }

    @Test
    void shouldReturn405ForUnsupportedMethod() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/ad/C123/clicks"))
                .andExpect(status().isMethodNotAllowed());
    }
}
