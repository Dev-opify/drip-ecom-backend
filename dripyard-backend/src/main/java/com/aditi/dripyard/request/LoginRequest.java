package com.aditi.dripyard.request;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginRequest {

    private String email;
    private String password;
    private String otp;

}