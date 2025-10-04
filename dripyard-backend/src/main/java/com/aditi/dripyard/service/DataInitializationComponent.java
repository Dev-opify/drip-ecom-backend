package com.aditi.dripyard.service;


import com.aditi.dripyard.domain.USER_ROLE;
import com.aditi.dripyard.model.User;
import com.aditi.dripyard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializationComponent implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initializeAdminUser();
        initializeDummyUser();
    }

    private void initializeAdminUser() {
        String adminUsername = "admin@dripyard.in";

        User existingAdmin = userRepository.findByEmail(adminUsername);
        if (existingAdmin == null) {
            // Create new admin user
            User adminUser = new User();
            adminUser.setPassword(passwordEncoder.encode("123456"));
            adminUser.setFullName("Dripyard Admin");
            adminUser.setEmail(adminUsername);
            adminUser.setRole(USER_ROLE.ROLE_ADMIN);
            userRepository.save(adminUser);
            System.out.println("Created new admin user: " + adminUsername);
        } else {
            // Update existing user to ensure admin role
            existingAdmin.setRole(USER_ROLE.ROLE_ADMIN);
            existingAdmin.setPassword(passwordEncoder.encode("123456"));
            userRepository.save(existingAdmin);
            System.out.println("Updated existing user to admin: " + adminUsername);
        }
    }

    private void initializeDummyUser() {
        String dummyUserEmail = "testuser@dripyard.in";

        User existingUser = userRepository.findByEmail(dummyUserEmail);
        if (existingUser == null) {
            // Create new dummy user
            User dummyUser = new User();
            dummyUser.setPassword(passwordEncoder.encode("password123"));
            dummyUser.setFullName("Test User");
            dummyUser.setEmail(dummyUserEmail);
            dummyUser.setMobile("9876543210");
            dummyUser.setRole(USER_ROLE.ROLE_CUSTOMER);
            userRepository.save(dummyUser);
            System.out.println("Created new dummy user: " + dummyUserEmail);
        } else {
            // Update existing dummy user
            existingUser.setPassword(passwordEncoder.encode("password123"));
            existingUser.setRole(USER_ROLE.ROLE_CUSTOMER);
            userRepository.save(existingUser);
            System.out.println("Updated existing dummy user: " + dummyUserEmail);
        }
    }

}