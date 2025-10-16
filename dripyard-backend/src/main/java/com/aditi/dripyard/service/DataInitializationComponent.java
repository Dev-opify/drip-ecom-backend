package com.aditi.dripyard.service;


import com.aditi.dripyard.domain.USER_ROLE;
import com.aditi.dripyard.model.User;
import com.aditi.dripyard.model.VerificationCode;
import com.aditi.dripyard.repository.UserRepository;
import com.aditi.dripyard.repository.VerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializationComponent implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeRepository verificationCodeRepository;



    @Override
    public void run(String... args) {
        initializeTestAdminWithOtp();
    }

    private void initializeTestAdminWithOtp() {
        String testAdminEmail = "admin@dripyard.in";
        String testOtp = "123456";

        // Create test admin user if doesn't exist
        User testAdmin = userRepository.findByEmail(testAdminEmail);
        if (testAdmin == null) {
            testAdmin = new User();
            testAdmin.setPassword(passwordEncoder.encode("admin123")); // Backup password
            testAdmin.setFullName("Test Admin");
            testAdmin.setEmail(testAdminEmail);
            testAdmin.setRole(USER_ROLE.ROLE_ADMIN);
            testAdmin = userRepository.save(testAdmin);
        }

        // Create or update persistent test OTP for this admin
        VerificationCode existingCode = verificationCodeRepository.findByEmail(testAdminEmail);
        if (existingCode == null) {
            VerificationCode verificationCode = new VerificationCode();
            verificationCode.setEmail(testAdminEmail);
            verificationCode.setOtp(testOtp);
            verificationCode.setUser(testAdmin);
            verificationCodeRepository.save(verificationCode);
            System.out.println("✓ Test admin created: " + testAdminEmail + " with OTP: " + testOtp);
        } else {
            // Update OTP to ensure it's always 123456
            existingCode.setOtp(testOtp);
            verificationCodeRepository.save(existingCode);
            System.out.println("✓ Test admin OTP updated: " + testAdminEmail + " with OTP: " + testOtp);
        }
    }

}