// dripyard-backend/src/main/java/com/aditi/dripyard/dto/ShiprocketOrderResponseDto.java
package com.aditi.dripyard.dto;

public class ShiprocketOrderResponseDto {
    private boolean success;
    private String message;
    private String orderId;
    private String shipmentId;
    // Add other fields as required

    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getShipmentId() { return shipmentId; }
    public void setShipmentId(String shipmentId) { this.shipmentId = shipmentId; }
}
