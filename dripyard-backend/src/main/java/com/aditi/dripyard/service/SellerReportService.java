package com.aditi.dripyard.service;


import com.aditi.dripyard.model.Seller;
import com.aditi.dripyard.model.SellerReport;

public interface SellerReportService {
    SellerReport getSellerReport(Seller seller);
    SellerReport updateSellerReport( SellerReport sellerReport);

}
