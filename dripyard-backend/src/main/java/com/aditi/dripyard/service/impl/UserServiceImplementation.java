package com.aditi.dripyard.service.impl;

import com.aditi.dripyard.config.JwtProvider;
import com.aditi.dripyard.exception.UserException;
import com.aditi.dripyard.model.User;
import com.aditi.dripyard.repository.PasswordResetTokenRepository;
import com.aditi.dripyard.repository.UserRepository;
import com.aditi.dripyard.service.MailerSendService;
import com.aditi.dripyard.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImplementation implements UserService {

	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;
	private final PasswordEncoder passwordEncoder;
	private final PasswordResetTokenRepository passwordResetTokenRepository;
	private final MailerSendService mailerSendService;

	public UserServiceImplementation(
			UserRepository userRepository,
			JwtProvider jwtProvider,
			PasswordEncoder passwordEncoder,
			PasswordResetTokenRepository passwordResetTokenRepository,
			MailerSendService mailerSendService) {
		this.userRepository = userRepository;
		this.jwtProvider = jwtProvider;
		this.passwordEncoder = passwordEncoder;
		this.passwordResetTokenRepository = passwordResetTokenRepository;
		this.mailerSendService = mailerSendService;
	}

	@Override
	public User findUserProfileByJwt(String jwt) throws UserException {
		String email = jwtProvider.getEmailFromJwtToken(jwt);
		User user = userRepository.findByEmail(email);

		if (user == null) {
			throw new UserException("User does not exist with email " + email);
		}
		return user;
	}

	@Override
	public User findUserByEmail(String username) throws UserException {
		User user = userRepository.findByEmail(username);
		if (user != null) {
			return user;
		}
		throw new UserException("User does not exist with username " + username);
}

    @Override
    public User updateUserProfileByJwt(String jwt, com.aditi.dripyard.request.UpdateProfileRequest req) throws UserException {
        String email = jwtProvider.getEmailFromJwtToken(jwt);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserException("User does not exist with email " + email);
        }
        if (req.getFullName() != null && !req.getFullName().isBlank()) {
            user.setFullName(req.getFullName().trim());
        }
        if (req.getMobile() != null && !req.getMobile().isBlank()) {
            user.setMobile(req.getMobile().trim());
        }
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            // Optionally validate uniqueness/permission here
            user.setEmail(req.getEmail().trim());
        }
        return userRepository.save(user);
    }
}
