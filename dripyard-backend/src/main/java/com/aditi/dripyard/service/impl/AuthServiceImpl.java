package com.aditi.dripyard.service.impl;

import com.aditi.dripyard.config.JwtProvider;
import com.aditi.dripyard.domain.USER_ROLE;
import com.aditi.dripyard.exception.UserException;
import com.aditi.dripyard.model.Cart;
import com.aditi.dripyard.model.User;
import com.aditi.dripyard.model.VerificationCode;
import com.aditi.dripyard.repository.CartRepository;
import com.aditi.dripyard.repository.UserRepository;
import com.aditi.dripyard.repository.VerificationCodeRepository;
import com.aditi.dripyard.request.LoginRequest;
import com.aditi.dripyard.request.SignupRequest;
import com.aditi.dripyard.response.AuthResponse;
import com.aditi.dripyard.service.AuthService;
import com.aditi.dripyard.service.EmailService;
import com.aditi.dripyard.service.UserService;
import com.aditi.dripyard.utils.OtpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final CustomeUserServiceImplementation customUserDetails;
    private final CartRepository cartRepository;

    @Override
    public void sentLoginOtp(String email) throws UserException {
        try {
            String SIGNING_PREFIX = "signing_";

            if (email.startsWith(SIGNING_PREFIX)) {
                email = email.substring(SIGNING_PREFIX.length());
                userService.findUserByEmail(email);
            }

            VerificationCode existing = verificationCodeRepository.findByEmail(email);
            if (existing != null) {
                verificationCodeRepository.delete(existing);
            }

            String otp = OtpUtils.generateOTP();
            VerificationCode verificationCode = new VerificationCode();
            verificationCode.setOtp(otp);
            verificationCode.setEmail(email);
            verificationCodeRepository.save(verificationCode);

            String subject = "Dripyard Login/Signup OTP";
            String text = "Your login OTP is: ";
            emailService.sendVerificationOtpEmail(email, otp, subject, text);

        } catch (Exception e) {
            throw new UserException("Failed to send OTP: " + e.getMessage());
        }
    }

    @Override
    public String createUser(SignupRequest req) throws UserException {
        String email = req.getEmail();
        String fullName = req.getFullName();
        String otp = req.getOtp();

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(email);
        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new UserException("Wrong OTP...");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            User createdUser = new User();
            createdUser.setEmail(email);
            createdUser.setFullName(fullName);
            createdUser.setRole(USER_ROLE.ROLE_CUSTOMER);
            createdUser.setMobile("9083476123");
            createdUser.setPassword(passwordEncoder.encode(otp));

            user = userRepository.save(createdUser);

            Cart cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_CUSTOMER.toString()));

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtProvider.generateToken(authentication);
    }

    @Override
    public AuthResponse signin(LoginRequest req) throws UserException {
        String username = req.getEmail();
        String password = req.getPassword();
        String otp = req.getOtp();

        Authentication authentication;
        
        // Check if using OTP or password authentication
        if (otp != null && !otp.trim().isEmpty()) {
            // OTP-based authentication
            authentication = authenticateWithOtp(username, otp);
        } else if (password != null && !password.trim().isEmpty()) {
            // Password-based authentication
            authentication = authenticateWithPassword(username, password);
        } else {
            throw new UserException("Please provide either password or OTP for login");
        }
        
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setMessage("Login Success");
        authResponse.setJwt(token);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roleName = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();
        authResponse.setRole(USER_ROLE.valueOf(roleName));

        return authResponse;
    }

    private Authentication authenticateWithPassword(String username, String password) throws UserException {
        UserDetails userDetails = customUserDetails.loadUserByUsername(username);
        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username or password");
        }

        // Verify password using password encoder
        User user = userRepository.findByEmail(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new UserException("Invalid username or password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private Authentication authenticateWithOtp(String username, String otp) throws UserException {
        UserDetails userDetails = customUserDetails.loadUserByUsername(username);
        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username or password");
        }

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(username);
        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new UserException("Wrong OTP...");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
