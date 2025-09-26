package com.aditi.dripyard.service.impl;

import com.aditi.dripyard.config.JwtProvider;
import com.aditi.dripyard.exception.UserException;
import com.aditi.dripyard.model.User;
import com.aditi.dripyard.repository.PasswordResetTokenRepository;
import com.aditi.dripyard.repository.UserRepository;
import com.aditi.dripyard.service.MailerSendService;
import com.aditi.dripyard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {

	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;
	private final PasswordEncoder passwordEncoder;
	private final PasswordResetTokenRepository passwordResetTokenRepository;
	private final MailerSendService mailerSendService;

	@Override
	public User findUserProfileByJwt(String jwt) throws UserException {
		String email = jwtProvider.getEmailFromJwtToken(jwt);
		User user = userRepository.findByEmail(email);

		if (user == null) {
			throw new UserException("user not exist with email " + email);
		}
		return user;
	}

	@Override
	public User findUserByEmail(String username) throws UserException {
		User user = userRepository.findByEmail(username);

		if (user != null) {
			return user;
		}

		throw new UserException("user not exist with username " + username);
	}
}

