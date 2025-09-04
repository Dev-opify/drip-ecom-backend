package com.aditi.dripyard.service.impl;

import com.aditi.dripyard.config.JwtProvider;
import com.aditi.dripyard.domain.AccountStatus;
import com.aditi.dripyard.model.Address;
import com.aditi.dripyard.model.Seller;
import com.aditi.dripyard.repository.AddressRepository;
import com.aditi.dripyard.repository.SellerRepository;
import com.aditi.dripyard.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor

public class SellerServiceImpl implements SellerService {

    private final SellerRepository sellerRepository;

    private final JwtProvider jwtProvider;

    private final PasswordEncoder passwordEncoder;

    private final AddressRepository addressRepository;




    @Override
    public Seller getSellerProfile(String jwt) throws Exception {
        String email= jwtProvider.getEmailFromJwtToken(jwt);

        return this.getSellerByEmail(email);
    }

    @Override
    public Seller createSeller(Seller seller) throws Exception {
        Seller sellerExist= sellerRepository.findByEmail(seller.getEmail());
        if(sellerExist!= null) {
            throw new Exception("seller already exists, use different email");
        }
        Address savedAddress = addressRepository.save(seller.getPickupAddress());

        Seller newSeller = new Seller();
        newSeller.setEmail(seller.getEmail());
        newSeller.setPassword(passwordEncoder.encode(seller ));
        return null;
    }

    @Override
    public Seller getSellerById(Long id) {
        return null;
    }

    @Override
    public Seller getSellerByEmail(String email) throws Exception {
        Seller seller = sellerRepository.findByEmail(email);
        if(seller == null) {

            throw new Exception("seller not found");

        }
        return seller;
    }

    @Override
    public List<Seller> getAllSellers(AccountStatus status) {
        return List.of();
    }

    @Override
    public Seller updateSeller(Long id, Seller seller) {
        return null;
    }

    @Override
    public void deleteSeller(Long id) {

    }

    @Override
    public Seller verifyEmail(String email, String otp) {
        return null;
    }

    @Override
    public Seller updateSellerAccountStatus(Long sellerId, AccountStatus status) {
        return null;
    }
}
