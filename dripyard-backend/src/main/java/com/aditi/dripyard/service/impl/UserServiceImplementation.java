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
	public java.util.List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public void deleteUser(Long userId) throws UserException {
		User user = userRepository.findById(userId).orElseThrow(() -> new UserException("User not found with id " + userId));
		userRepository.delete(user);
	}

	@Override
	public User updateUser(User user) throws UserException {
		return userRepository.save(user);
	}

	@Override
	public void changePassword(User user, String currentPassword, String newPassword) throws UserException {
		// Verify current password
		if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
			throw new UserException("Current password is incorrect");
		}
		
		// Update to new password
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}
}
