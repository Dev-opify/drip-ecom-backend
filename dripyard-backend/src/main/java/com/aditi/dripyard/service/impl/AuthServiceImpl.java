package com.aditi.dripyard.service.impl;

import com.aditi.dripyard.config.JwtProvider;
import com.aditi.dripyard.domain.USER_ROLE;
import com.aditi.dripyard.exception.SellerException;
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
<<<<<<< HEAD
import com.aditi.dripyard.service.MailerSendService;
import com.aditi.dripyard.service.UserService;
import com.aditi.dripyard.utils.OtpUtils;
=======
import com.aditi.dripyard.service.EmailService;
import com.aditi.dripyard.service.UserService;
import com.aditi.dripyard.utils.OtpUtils;
import jakarta.mail.MessagingException;
>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
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
<<<<<<< HEAD
    private final VerificationCodeRepository verificationCodeRepository;
    private final MailerSendService mailerSendService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
=======

    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
    private final JwtProvider jwtProvider;
    private final CustomeUserServiceImplementation customUserDetails;
    private final CartRepository cartRepository;

<<<<<<< HEAD
    @Override
    public void sentLoginOtp(String email) throws UserException {
        try {
            String SIGNING_PREFIX = "signing_";

            if (email.startsWith(SIGNING_PREFIX)) {
                email = email.substring(SIGNING_PREFIX.length());
                userService.findUserByEmail(email);
            }

            VerificationCode isExist = verificationCodeRepository.findByEmail(email);
            if (isExist != null) {
                verificationCodeRepository.delete(isExist);
            }

            String otp = OtpUtils.generateOTP();

            VerificationCode verificationCode = new VerificationCode();
            verificationCode.setOtp(otp);
            verificationCode.setEmail(email);
            verificationCodeRepository.save(verificationCode);

            mailerSendService.sendEmail(
                    email,
                    "Dripyard Login/Signup OTP",
                    "Your login OTP is: " + otp
            );
        } catch (Exception e) {
            throw new UserException("Failed to send OTP: " + e.getMessage());
        }
=======

    @Override
    public void sentLoginOtp(String email) throws UserException, MessagingException {


        String SIGNING_PREFIX = "signing_";

        if (email.startsWith(SIGNING_PREFIX)) {
            email = email.substring(SIGNING_PREFIX.length());
            userService.findUserByEmail(email);
        }

        VerificationCode isExist = verificationCodeRepository
                .findByEmail(email);

        if (isExist != null) {
            verificationCodeRepository.delete(isExist);
        }

        String otp = OtpUtils.generateOTP();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(email);
        verificationCodeRepository.save(verificationCode);

        String subject = "Dripyard Login/Signup Otp";
        String text = "your login otp is - ";
        emailService.sendVerificationOtpEmail(email, otp, subject, text);
>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
    }

    @Override
    public String createUser(SignupRequest req) throws SellerException {
<<<<<<< HEAD
        String email = req.getEmail();
        String fullName = req.getFullName();
=======

        String email = req.getEmail();

        String fullName = req.getFullName();

>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
        String otp = req.getOtp();

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(email);

        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new SellerException("wrong otp...");
        }

        User user = userRepository.findByEmail(email);

        if (user == null) {
<<<<<<< HEAD
=======

>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
            User createdUser = new User();
            createdUser.setEmail(email);
            createdUser.setFullName(fullName);
            createdUser.setRole(USER_ROLE.ROLE_CUSTOMER);
<<<<<<< HEAD
            createdUser.setMobile("6392077114");
            createdUser.setPassword(passwordEncoder.encode(otp));

=======
            createdUser.setMobile("9083476123");
            createdUser.setPassword(passwordEncoder.encode(otp));

            System.out.println(createdUser);

>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
            user = userRepository.save(createdUser);

            Cart cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
<<<<<<< HEAD

            try {
                mailerSendService.sendEmail(
                        email,
                        "Welcome to Dripyard",
                        "Thank you for registering with Dripyard, " + fullName + "!"
                );
            } catch (Exception e) {
                // Log error but continue with registration
            }
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_CUSTOMER.toString()));
=======
        }


        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority(
                USER_ROLE.ROLE_CUSTOMER.toString()));

>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                email, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtProvider.generateToken(authentication);
    }

    @Override
    public AuthResponse signin(LoginRequest req) throws SellerException {
<<<<<<< HEAD
        String username = req.getEmail();
        String otp = req.getOtp();

=======

        String username = req.getEmail();
        String otp = req.getOtp();

        System.out.println(username + " ----- " + otp);

>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
        Authentication authentication = authenticate(username, otp);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse();

        authResponse.setMessage("Login Success");
        authResponse.setJwt(token);
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

<<<<<<< HEAD
        String roleName = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();
        authResponse.setRole(USER_ROLE.valueOf(roleName));

        return authResponse;
    }

    private Authentication authenticate(String username, String otp) throws SellerException {
        UserDetails userDetails = customUserDetails.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username or password");
        }

=======

        String roleName = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();


        authResponse.setRole(USER_ROLE.valueOf(roleName));

        return authResponse;

    }



    private Authentication authenticate(String username, String otp) throws SellerException {
        UserDetails userDetails = customUserDetails.loadUserByUsername(username);

        System.out.println("sign in userDetails - " + userDetails);

        if (userDetails == null) {
            System.out.println("sign in userDetails - null ");
            throw new BadCredentialsException("Invalid username or password");
        }
>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(username);

        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new SellerException("wrong otp...");
        }
<<<<<<< HEAD

=======
>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
