package com.aditi.dripyard.controller;

import com.aditi.dripyard.config.JwtProvider;
import com.aditi.dripyard.domain.AccountStatus;
import com.aditi.dripyard.domain.USER_ROLE;
import com.aditi.dripyard.exception.SellerException;
import com.aditi.dripyard.model.Seller;
import com.aditi.dripyard.model.SellerReport;
import com.aditi.dripyard.model.VerificationCode;
import com.aditi.dripyard.repository.VerificationCodeRepository;
import com.aditi.dripyard.response.ApiResponse;
import com.aditi.dripyard.response.AuthResponse;
import com.aditi.dripyard.service.SellerReportService;
import com.aditi.dripyard.service.SellerService;
import com.aditi.dripyard.service.VerificationService;
import com.aditi.dripyard.service.impl.CustomeUserServiceImplementation;
import com.aditi.dripyard.utils.OtpUtils;
import com.aditi.dripyard.service.MailerSendService;
import com.mailersend.sdk.exceptions.MailerSendException;
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

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/sellers")
@RequiredArgsConstructor
public class SellerController {
    private final SellerService sellerService;
    private final SellerReportService sellerReportService;
    private final MailerSendService mailerSendService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final VerificationService verificationService;
    private final JwtProvider jwtProvider;
    private final CustomeUserServiceImplementation customeUserServiceImplementation;

    @PostMapping("/sent/login-otp")
    public ResponseEntity<ApiResponse> sentLoginOtp(@RequestBody VerificationCode req) throws SellerException {
        try {
            Seller seller = sellerService.getSellerByEmail(req.getEmail());
            String otp = OtpUtils.generateOTP();
            VerificationCode verificationCode = verificationService.createVerificationCode(otp, req.getEmail());

            String subject = "Dripyard Login OTP";
            String text = "Your login OTP is: " + verificationCode.getOtp();
            mailerSendService.sendEmail(req.getEmail(), subject, text);

            ApiResponse res = new ApiResponse();
            res.setMessage("OTP sent");
            return new ResponseEntity<>(res, HttpStatus.CREATED);
        } catch (MailerSendException e) {
            throw new SellerException("Failed to send OTP: " + e.getMessage());
        }
    }

    @PostMapping("/verify/login-otp")
    public ResponseEntity<AuthResponse> verifyLoginOtp(@RequestBody VerificationCode req) throws SellerException {
        String otp = req.getOtp();
        String email = req.getEmail();
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(email);

        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new SellerException("Wrong OTP");
        }

        Authentication authentication = authenticate(req.getEmail());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setMessage("Login Success");
        authResponse.setJwt(token);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roleName = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();
        authResponse.setRole(USER_ROLE.valueOf(roleName));

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    private Authentication authenticate(String username) {
        UserDetails userDetails = customeUserServiceImplementation.loadUserByUsername("seller_" + username);
        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username or password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @PatchMapping("/verify/{otp}")
    public ResponseEntity<Seller> verifySellerEmail(@PathVariable String otp) throws SellerException {
        VerificationCode verificationCode = verificationCodeRepository.findByOtp(otp);
        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new SellerException("Wrong OTP");
        }
        Seller seller = sellerService.verifyEmail(verificationCode.getEmail(), otp);
        return new ResponseEntity<>(seller, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Seller> createSeller(@RequestBody Seller seller) throws SellerException {
        try {
            Seller savedSeller = sellerService.createSeller(seller);

            String otp = OtpUtils.generateOTP();
            VerificationCode verificationCode = verificationService.createVerificationCode(otp, seller.getEmail());

            String subject = "Dripyard Email Verification Code";
            String frontendUrl = "http://localhost:3000/verify-seller/";
            String text = "Welcome to Dripyard, verify your account using this link: " +
                    frontendUrl + verificationCode.getOtp();

            mailerSendService.sendEmail(seller.getEmail(), subject, text);

            return new ResponseEntity<>(savedSeller, HttpStatus.CREATED);
        } catch (MailerSendException e) {
            throw new SellerException("Failed to create seller: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Seller> getSellerById(@PathVariable Long id) throws SellerException {
        Seller seller = sellerService.getSellerById(id);
        return new ResponseEntity<>(seller, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<Seller> getSellerByJwt(@RequestHeader("Authorization") String jwt) throws SellerException {
        String email = jwtProvider.getEmailFromJwtToken(jwt);
        Seller seller = sellerService.getSellerByEmail(email);
        return new ResponseEntity<>(seller, HttpStatus.OK);
    }

    @GetMapping("/report")
    public ResponseEntity<SellerReport> getSellerReport(@RequestHeader("Authorization") String jwt) throws SellerException {
        String email = jwtProvider.getEmailFromJwtToken(jwt);
        Seller seller = sellerService.getSellerByEmail(email);
        SellerReport report = sellerReportService.getSellerReport(seller);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Seller>> getAllSellers(@RequestParam(required = false) AccountStatus status) {
        List<Seller> sellers = sellerService.getAllSellers(status);
        return ResponseEntity.ok(sellers);
    }

    @PatchMapping
    public ResponseEntity<Seller> updateSeller(@RequestHeader("Authorization") String jwt, @RequestBody Seller seller) throws SellerException {
        Seller profile = sellerService.getSellerProfile(jwt);
        Seller updatedSeller = sellerService.updateSeller(profile.getId(), seller);
        return ResponseEntity.ok(updatedSeller);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) throws SellerException {
        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }
}
