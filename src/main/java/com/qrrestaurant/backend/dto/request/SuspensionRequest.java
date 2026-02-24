package com.qrrestaurant.backend.dto.request;

public class SuspensionRequest {
    private String reason;

    // Default constructor
    public SuspensionRequest() {}

    // Parameterized constructor
    public SuspensionRequest(String reason) {
        this.reason = reason;
    }

    // Getter
    public String getReason() {
        return reason;
    }

    // Setter
    public void setReason(String reason) {
        this.reason = reason;
    }
}