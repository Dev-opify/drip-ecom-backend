package com.aditi.dripyard.service;


import com.aditi.dripyard.exception.UserException;
import com.aditi.dripyard.model.User;

public interface UserService {

    public User findUserProfileByJwt(String jwt) throws UserException;

    public User findUserByEmail(String email) throws UserException;


}
