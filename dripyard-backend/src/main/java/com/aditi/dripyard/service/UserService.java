package com.aditi.dripyard.service;


import com.aditi.dripyard.exception.UserException;
import com.aditi.dripyard.model.User;

public interface UserService {

    User findUserProfileByJwt(String jwt) throws UserException;

    User findUserByEmail(String email) throws UserException;

    User updateUserProfileByJwt(String jwt, com.aditi.dripyard.request.UpdateProfileRequest req) throws UserException;
}
