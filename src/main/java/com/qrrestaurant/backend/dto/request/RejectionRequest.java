package com.qrrestaurant.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RejectionRequest {

    @NotBlank(message = "Rejection reason must not be empty")
    @Size(max = 255, message = "Rejection reason must not exceed 255 characters")
    private String reason;

    public RejectionRequest() {
    }

    public RejectionRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}