package com.calendar.dto;

import com.calendar.entity.EventShare;

// Additional DTO Classes for Event Sharing
public class ShareEventRequest {
    private String userEmail;
    private EventShare.Permission permission;

    // Getters and setters
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public EventShare.Permission getPermission() {
        return permission;
    }

    public void setPermission(EventShare.Permission permission) {
        this.permission = permission;
    }
}
