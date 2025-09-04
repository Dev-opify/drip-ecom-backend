package com.aditi.dripyard.response;

import com.zosh.dto.OrderHistory;
import com.zosh.model.Cart;
import com.zosh.model.Product;
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
