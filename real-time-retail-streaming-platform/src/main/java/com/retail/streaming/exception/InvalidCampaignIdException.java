package com.retail.streaming.exception;

public class InvalidCampaignIdException extends RuntimeException {

    public InvalidCampaignIdException() {
        super("campaignId must be 1-64 characters: letters, digits, hyphen or underscore");
    }
}
