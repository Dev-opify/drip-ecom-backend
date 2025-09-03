package com.aditi.dripyard.service.impl;

import com.aditi.dripyard.domain.USER_ROLE;
import com.aditi.dripyard.model.Seller;
import com.aditi.dripyard.model.User;
import com.aditi.dripyard.repository.SellerRepository;
import com.aditi.dripyard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Service
public class CustomUserServiceImpl implements UserDetailsService {


    private final  UserRepository userRepository;

    private final SellerRepository sellerRepository;
    private static final String SELLER_PREFIX = "seller_";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

      if(username.startsWith(SELLER_PREFIX)){

          String actualUsername = username.substring(SELLER_PREFIX.length());
          Seller seller = sellerRepository.findByEmail(actualUsername);


          if(seller!= null){

              return buildUserDetails(seller.getEmail() , seller.getPassword() , seller.getRole());
            }

      } else{
          User user=  userRepository.findByEmail(username);
          if(user!= null){

              return buildUserDetails(user.getEmail() , user.getPassword() , user.getRole());


          }


      }

          throw new UsernameNotFoundException("User not found with email: " + username);
    }

    // dripyard-backend/src/main/java/com/aditi/dripyard/service/impl/CustomUserServiceImpl.java
    private UserDetails buildUserDetails(String email, String password, USER_ROLE role) {
        if (role == null) role = USER_ROLE.ROLE_CUSTOMER;
        if (email == null || email.isEmpty()) throw new IllegalArgumentException("Email cannot be null or empty");
        if (password == null || password.isEmpty()) password = "default_password"; // Set a default password

        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority(role.toString()));

        return new org.springframework.security.core.userdetails.User(email, password, authorityList);
    }
}
