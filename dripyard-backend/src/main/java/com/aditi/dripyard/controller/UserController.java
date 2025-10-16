package com.aditi.dripyard.controller;

import com.aditi.dripyard.exception.UserException;
import com.aditi.dripyard.model.User;
import com.aditi.dripyard.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService=userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfileHandler(
            @RequestHeader("Authorization") String jwt) throws UserException {

        System.out.println("/api/users/profile");
        User user=userService.findUserProfileByJwt(jwt);
        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateUserProfileHandler(
            @RequestHeader("Authorization") String jwt,
            @RequestBody User user) throws UserException {
        User currentUser = userService.findUserProfileByJwt(jwt);
        currentUser.setFullName(user.getFullName());
        currentUser.setMobile(user.getMobile());
        // Email is not updated to avoid changing the login identifier
        User updatedUser = userService.updateUser(currentUser);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePasswordHandler(
            @RequestHeader("Authorization") String jwt,
            @RequestBody PasswordChangeRequest request) throws UserException {
        User currentUser = userService.findUserProfileByJwt(jwt);
        userService.changePassword(currentUser, request.getCurrentPassword(), request.getNewPassword());
        return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
    }

    // Inner class for password change request
    public static class PasswordChangeRequest {
        private String currentPassword;
        private String newPassword;

        public String getCurrentPassword() {
            return currentPassword;
        }

        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}
