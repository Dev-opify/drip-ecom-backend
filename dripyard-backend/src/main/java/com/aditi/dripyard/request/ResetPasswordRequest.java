package com.aditi.dripyard.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
	
	private String password;
	private String token;


}
