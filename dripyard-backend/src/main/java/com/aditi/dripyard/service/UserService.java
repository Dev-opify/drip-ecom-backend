package com.aditi.dripyard.service;


import com.aditi.dripyard.exception.UserException;
import com.aditi.dripyard.model.User;

import java.util.List;

public interface UserService {

    public User findUserProfileByJwt(String jwt) throws UserException;

    public User findUserByEmail(String email) throws UserException;

    public List<User> getAllUsers();

    public void deleteUser(Long userId) throws UserException;

    public User updateUser(User user) throws UserException;

    public void changePassword(User user, String currentPassword, String newPassword) throws UserException;
}
