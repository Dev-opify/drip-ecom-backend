package com.aditi.dripyard.service;

import com.aditi.dripyard.model.User;

public interface UserService {
    User findUserByJwtToken(String jwt) throws Exception;
    User findUserByEmail(String email) throws Exception;
}
