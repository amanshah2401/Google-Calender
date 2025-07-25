package com.calendar.dto;

import com.calendar.entity.CalendarShare;

public class ShareCalendarRequest {
    private String userEmail;
    private CalendarShare.Permission permission;

    // Getters and setters
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public CalendarShare.Permission getPermission() {
        return permission;
    }

    public void setPermission(CalendarShare.Permission permission) {
        this.permission = permission;
    }
}
