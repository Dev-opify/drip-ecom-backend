package com.aditi.dripyard.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String mobile;
    private String email; // optional if allowed
}