package com.aditi.dripyard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class ShiprocketOrderRequestDto {

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("order_date")
    private String orderDate;

    @JsonProperty("pickup_location")
    private String pickupLocation;

    @JsonProperty("channel_id")
    private String channelId;

    @JsonProperty("billing_customer_name")
    private String billingCustomerName;

    @JsonProperty("billing_address")
    private String billingAddress;

    @JsonProperty("billing_city")
    private String billingCity;

    @JsonProperty("billing_state")
    private String billingState;

    @JsonProperty("billing_country")
    private String billingCountry;

    @JsonProperty("billing_pincode")
    private String billingPincode;

    @JsonProperty("billing_phone")
    private String billingPhone;

    @JsonProperty("shipping_is_billing")
    private boolean shippingIsBilling = true;

    @JsonProperty("payment_method")
    private String paymentMethod; // Prepaid or COD

    @JsonProperty("order_items")
    private List<ShiprocketOrderItemDto> orderItems = new ArrayList<>();

    @JsonProperty("sub_total")
    private Integer subTotal;

    @JsonProperty("discount")
    private Integer discount;

    @JsonProperty("shipping_charges")
    private Integer shippingCharges;

    @JsonProperty("length")
    private Double length;

    @JsonProperty("breadth")
    private Double breadth;

    @JsonProperty("height")
    private Double height;

    @JsonProperty("weight")
    private Double weight;

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }

    public String getChannelId() { return channelId; }
    public void setChannelId(String channelId) { this.channelId = channelId; }

    public String getBillingCustomerName() { return billingCustomerName; }
    public void setBillingCustomerName(String billingCustomerName) { this.billingCustomerName = billingCustomerName; }

    public String getBillingAddress() { return billingAddress; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }

    public String getBillingCity() { return billingCity; }
    public void setBillingCity(String billingCity) { this.billingCity = billingCity; }

    public String getBillingState() { return billingState; }
    public void setBillingState(String billingState) { this.billingState = billingState; }

    public String getBillingCountry() { return billingCountry; }
    public void setBillingCountry(String billingCountry) { this.billingCountry = billingCountry; }

    public String getBillingPincode() { return billingPincode; }
    public void setBillingPincode(String billingPincode) { this.billingPincode = billingPincode; }

    public String getBillingPhone() { return billingPhone; }
    public void setBillingPhone(String billingPhone) { this.billingPhone = billingPhone; }

    public boolean isShippingIsBilling() { return shippingIsBilling; }
    public void setShippingIsBilling(boolean shippingIsBilling) { this.shippingIsBilling = shippingIsBilling; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public List<ShiprocketOrderItemDto> getOrderItems() { return orderItems; }
    public void setOrderItems(List<ShiprocketOrderItemDto> orderItems) { this.orderItems = orderItems; }

    public Integer getSubTotal() { return subTotal; }
    public void setSubTotal(Integer subTotal) { this.subTotal = subTotal; }

    public Integer getDiscount() { return discount; }
    public void setDiscount(Integer discount) { this.discount = discount; }

    public Integer getShippingCharges() { return shippingCharges; }
    public void setShippingCharges(Integer shippingCharges) { this.shippingCharges = shippingCharges; }

    public Double getLength() { return length; }
    public void setLength(Double length) { this.length = length; }

    public Double getBreadth() { return breadth; }
    public void setBreadth(Double breadth) { this.breadth = breadth; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
}
