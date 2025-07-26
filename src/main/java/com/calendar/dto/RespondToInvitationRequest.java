package com.calendar.dto;

import com.calendar.entity.EventAttendee;

public class RespondToInvitationRequest {
    private EventAttendee.ResponseStatus response;

    // Getters and setters
    public EventAttendee.ResponseStatus getResponse() {
        return response;
    }

    public void setResponse(EventAttendee.ResponseStatus response) {
        this.response = response;
    }
}
