package com.aditi.dripyard.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SignupRequest {
    private String fullName;
    private String email;
    private String otp;
}
