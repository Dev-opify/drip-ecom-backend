package com.aditi.dripyard.service.impl;

import com.aditi.dripyard.config.JwtProvider;
import com.aditi.dripyard.domain.USER_ROLE;
import com.aditi.dripyard.model.Cart;
import com.aditi.dripyard.model.User;
import com.aditi.dripyard.model.VerificationCode;
import com.aditi.dripyard.repository.CartRepository;
import com.aditi.dripyard.repository.UserRepository;
import com.aditi.dripyard.repository.VerificationCodeRepository;
import com.aditi.dripyard.request.LoginRequest;
import com.aditi.dripyard.response.AuthResponse;
import com.aditi.dripyard.response.SignupRequest;
import com.aditi.dripyard.service.AuthService;
import com.aditi.dripyard.service.EmailService;
import com.aditi.dripyard.utils.OtpUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final CartRepository cartRepository;

    private final JwtProvider jwtProvider;

    private final VerificationCodeRepository verificationCodeRepository;

    private final EmailService emailService;

    private final CustomUserServiceImpl customUserService;


    @Override
    public void sentLoginOtp(String email) throws MessagingException {
        String SIGNING_PREFIX = "signin_";


        if(email.startsWith(SIGNING_PREFIX)){
            email = email.substring(SIGNING_PREFIX.length());

            User user = userRepository.findByEmail(email);
            if(user==null){
                throw new RuntimeException("User does not exist with the provided email");

            }
        }


        VerificationCode isExist = verificationCodeRepository.findByEmail(email);


        if(isExist != null){

            verificationCodeRepository.delete(isExist);

        }

        String otp = OtpUtil.generateOtp();

        VerificationCode verificationCode = new VerificationCode();

        verificationCode.setOtp(otp);

        verificationCode.setEmail(email);

        verificationCodeRepository.save(verificationCode);

        String subject= "Your DripYard Login/Signup OTP";
        String text = "Your One Time Password(OTP) for login/signup is: " + otp + ". It is valid for 10 minutes. Please do not share it with anyone.";

        emailService.sendVerificationOtpEmail(email, otp , subject, text);





    }

    @Override
    public String createUser(SignupRequest req) throws Exception {

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(req.getEmail());

        if(verificationCode == null || !verificationCode.getOtp().equals(req.getOtp())){

            throw new Exception("Wrong OTP");

        }

        User user = userRepository.findByEmail(req.getEmail());

        if(user == null){
           User createdUser = new User();
              createdUser.setEmail(req.getEmail());
                createdUser.setFullName(req.getFullName());
                createdUser.setMobile("1234567890");
                createdUser.setPassword(passwordEncoder.encode(req.getOtp()));

                user = userRepository.save(createdUser);

            Cart cart = new Cart();

            cart.setUser(user);

            cartRepository.save(cart);



        }

        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_CUSTOMER.toString() ));


        Authentication authentication = new UsernamePasswordAuthenticationToken(req.getEmail(), null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);





        return jwtProvider.generateToken(authentication);
    }

    @Override
    public AuthResponse signing(LoginRequest req) {

        String username =  req.getEmail();
        String otp = req.getOtp();



        Authentication authentication = authenticate(username, otp );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Login success");

        Collection<?extends GrantedAuthority> authorities = authentication.getAuthorities();

        String roleName = authorities.isEmpty()?null:authorities.iterator().next().getAuthority();

        authResponse.setRole(USER_ROLE.valueOf(roleName));

        return authResponse;


    }

    private Authentication authenticate(String username, String otp) {

        UserDetails  userDetails=   customUserService.loadUserByUsername(username);

        if(userDetails == null){
            throw new BadCredentialsException("Invalid username");


        }

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(username);

        if(verificationCode == null || !verificationCode.getOtp().equals(otp) ){
            throw new BadCredentialsException("Wrong OTP");
        }


        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());



    }
}
