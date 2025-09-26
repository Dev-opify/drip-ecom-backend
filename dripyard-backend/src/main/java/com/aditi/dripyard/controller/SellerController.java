package com.aditi.dripyard.controller;

import com.aditi.dripyard.config.JwtProvider;
import com.aditi.dripyard.domain.AccountStatus;
import com.aditi.dripyard.domain.USER_ROLE;
import com.aditi.dripyard.exception.SellerException;
import com.aditi.dripyard.model.Seller;
import com.aditi.dripyard.model.SellerReport;
import com.aditi.dripyard.model.VerificationCode;
import com.aditi.dripyard.repository.VerificationCodeRepository;
<<<<<<< HEAD
import com.aditi.dripyard.response.ApiResponse;
import com.aditi.dripyard.response.AuthResponse;
import com.aditi.dripyard.service.MailerSendService;
import com.aditi.dripyard.service.SellerReportService;
import com.aditi.dripyard.service.SellerService;
import com.aditi.dripyard.service.VerificationService;
import com.aditi.dripyard.service.impl.CustomeUserServiceImplementation;
import com.aditi.dripyard.utils.OtpUtils;
=======

import com.aditi.dripyard.response.ApiResponse;
import com.aditi.dripyard.response.AuthResponse;
import com.aditi.dripyard.service.EmailService;
import com.aditi.dripyard.service.SellerReportService;
import com.aditi.dripyard.service.SellerService;
import com.aditi.dripyard.service.VerificationService;


import com.aditi.dripyard.service.impl.CustomeUserServiceImplementation;
import jakarta.mail.MessagingException;
>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
<<<<<<< HEAD
=======
import com.aditi.dripyard.utils.OtpUtils;
>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;
    private final SellerReportService sellerReportService;
<<<<<<< HEAD
    private final MailerSendService mailerSendService;
=======
    private final EmailService emailService;
>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
    private final VerificationCodeRepository verificationCodeRepository;
    private final VerificationService verificationService;
    private final JwtProvider jwtProvider;
    private final CustomeUserServiceImplementation customeUserServiceImplementation;

<<<<<<< HEAD
    @PostMapping("/sent/login-top")
    public ResponseEntity<ApiResponse> sentLoginOtp(@RequestBody VerificationCode req) throws SellerException {
        try {
            Seller seller = sellerService.getSellerByEmail(req.getEmail());
            String otp = OtpUtils.generateOTP();
            VerificationCode verificationCode = verificationService.createVerificationCode(otp, req.getEmail());

            mailerSendService.sendEmail(
                    req.getEmail(),
                    "Dripyard Login OTP",
                    "Your login OTP is: " + verificationCode.getOtp()
            );

            ApiResponse res = new ApiResponse();
            res.setMessage("otp sent");
            return new ResponseEntity<>(res, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new SellerException("Failed to send OTP: " + e.getMessage());
        }
    }

    @PostMapping("/verify/login-top")
    public ResponseEntity<AuthResponse> verifyLoginOtp(@RequestBody VerificationCode req) throws SellerException {
=======

    @PostMapping("/sent/login-top")
    public ResponseEntity<ApiResponse> sentLoginOtp(@RequestBody VerificationCode req) throws MessagingException, SellerException {
        Seller seller = sellerService.getSellerByEmail(req.getEmail());

        String otp = OtpUtils.generateOTP();
        VerificationCode verificationCode = verificationService.createVerificationCode(otp, req.getEmail());

        String subject = "Dripyard Login Otp";
        String text = "your login otp is - ";
        emailService.sendVerificationOtpEmail(req.getEmail(), verificationCode.getOtp(), subject, text);

        ApiResponse res = new ApiResponse();
        res.setMessage("otp sent");
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/verify/login-top")
    public ResponseEntity<AuthResponse> verifyLoginOtp(@RequestBody VerificationCode req) throws MessagingException, SellerException {
//        Seller savedSeller = sellerService.createSeller(seller);


>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
        String otp = req.getOtp();
        String email = req.getEmail();
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(email);

        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new SellerException("wrong otp...");
        }

        Authentication authentication = authenticate(req.getEmail());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse();
<<<<<<< HEAD
        authResponse.setMessage("Login Success");
        authResponse.setJwt(token);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roleName = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();
        authResponse.setRole(USER_ROLE.valueOf(roleName));

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
=======

        authResponse.setMessage("Login Success");
        authResponse.setJwt(token);
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();


        String roleName = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();


        authResponse.setRole(USER_ROLE.valueOf(roleName));

        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);
>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
    }

    private Authentication authenticate(String username) {
        UserDetails userDetails = customeUserServiceImplementation.loadUserByUsername("seller_" + username);

<<<<<<< HEAD
        if (userDetails == null) {
=======
        System.out.println("sign in userDetails - " + userDetails);

        if (userDetails == null) {
            System.out.println("sign in userDetails - null " + userDetails);
>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
            throw new BadCredentialsException("Invalid username or password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @PatchMapping("/verify/{otp}")
    public ResponseEntity<Seller> verifySellerEmail(@PathVariable String otp) throws SellerException {
<<<<<<< HEAD
=======


>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
        VerificationCode verificationCode = verificationCodeRepository.findByOtp(otp);

        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new SellerException("wrong otp...");
        }

        Seller seller = sellerService.verifyEmail(verificationCode.getEmail(), otp);
<<<<<<< HEAD
        return new ResponseEntity<>(seller, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Seller> createSeller(@RequestBody Seller seller) throws SellerException {
        try {
            Seller savedSeller = sellerService.createSeller(seller);

            String otp = OtpUtils.generateOTP();
            VerificationCode verificationCode = verificationService.createVerificationCode(otp, seller.getEmail());

            String frontend_url = "http://localhost:3000/verify-seller/";
            mailerSendService.sendEmail(
                    seller.getEmail(),
                    "Dripyard Email Verification Code",
                    "Welcome to Dripyard! Verify your account using this link: " +
                            frontend_url + verificationCode.getOtp()
            );

            return new ResponseEntity<>(savedSeller, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new SellerException("Failed to create seller: " + e.getMessage());
        }
=======

        return new ResponseEntity<>(seller, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<Seller> createSeller(@RequestBody Seller seller) throws SellerException, MessagingException {
        Seller savedSeller = sellerService.createSeller(seller);

        String otp = OtpUtils.generateOTP();
        VerificationCode verificationCode = verificationService.createVerificationCode(otp, seller.getEmail());

        String subject = "Dripyard Email Verification Code";
        String text = "Welcome to Dripyard, verify your account using this link ";
        String frontend_url = "http://localhost:3000/verify-seller/";
        emailService.sendVerificationOtpEmail(seller.getEmail(), verificationCode.getOtp(), subject, text + frontend_url);
        return new ResponseEntity<>(savedSeller, HttpStatus.CREATED);
>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
    }

    @GetMapping("/{id}")
    public ResponseEntity<Seller> getSellerById(@PathVariable Long id) throws SellerException {
        Seller seller = sellerService.getSellerById(id);
        return new ResponseEntity<>(seller, HttpStatus.OK);
    }

    @GetMapping("/profile")
<<<<<<< HEAD
    public ResponseEntity<Seller> getSellerByJwt(@RequestHeader("Authorization") String jwt) throws SellerException {
=======
    public ResponseEntity<Seller> getSellerByJwt(
            @RequestHeader("Authorization") String jwt) throws SellerException {
>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
        String email = jwtProvider.getEmailFromJwtToken(jwt);
        Seller seller = sellerService.getSellerByEmail(email);
        return new ResponseEntity<>(seller, HttpStatus.OK);
    }

    @GetMapping("/report")
<<<<<<< HEAD
    public ResponseEntity<SellerReport> getSellerReport(@RequestHeader("Authorization") String jwt) throws SellerException {
=======
    public ResponseEntity<SellerReport> getSellerReport(
            @RequestHeader("Authorization") String jwt) throws SellerException {
>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
        String email = jwtProvider.getEmailFromJwtToken(jwt);
        Seller seller = sellerService.getSellerByEmail(email);
        SellerReport report = sellerReportService.getSellerReport(seller);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @GetMapping
<<<<<<< HEAD
    public ResponseEntity<List<Seller>> getAllSellers(@RequestParam(required = false) AccountStatus status) {
=======
    public ResponseEntity<List<Seller>> getAllSellers(
            @RequestParam(required = false) AccountStatus status) {
>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
        List<Seller> sellers = sellerService.getAllSellers(status);
        return ResponseEntity.ok(sellers);
    }

<<<<<<< HEAD
    @PatchMapping
    public ResponseEntity<Seller> updateSeller(@RequestHeader("Authorization") String jwt, @RequestBody Seller seller) throws SellerException {
        Seller profile = sellerService.getSellerProfile(jwt);
        Seller updatedSeller = sellerService.updateSeller(profile.getId(), seller);
        return ResponseEntity.ok(updatedSeller);
=======
    @PatchMapping()
    public ResponseEntity<Seller> updateSeller(
            @RequestHeader("Authorization") String jwt, @RequestBody Seller seller) throws SellerException {

        Seller profile = sellerService.getSellerProfile(jwt);
        Seller updatedSeller = sellerService.updateSeller(profile.getId(), seller);
        return ResponseEntity.ok(updatedSeller);

>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) throws SellerException {
<<<<<<< HEAD
        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
=======

        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();

>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
    }
}
