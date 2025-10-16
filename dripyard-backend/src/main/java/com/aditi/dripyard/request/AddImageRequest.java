package com.aditi.dripyard.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddImageRequest {
    
    private Long productId;
    private String imageKey;
    
}