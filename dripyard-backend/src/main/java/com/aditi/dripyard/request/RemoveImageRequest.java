package com.aditi.dripyard.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoveImageRequest {
    
    private Long productId;
    private String imageKey;
    
}