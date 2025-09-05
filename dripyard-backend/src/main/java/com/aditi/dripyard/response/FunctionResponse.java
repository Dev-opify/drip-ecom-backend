package com.aditi.dripyard.response;


import com.aditi.dripyard.dto.OrderHistory;
import com.aditi.dripyard.model.Cart;
import com.aditi.dripyard.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunctionResponse {
    private String functionName;
    private Cart userCart;
    private OrderHistory orderHistory;
    private Product product;
}
