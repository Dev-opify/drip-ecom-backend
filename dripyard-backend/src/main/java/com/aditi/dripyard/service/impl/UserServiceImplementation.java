package com.aditi.dripyard.service.impl;

<<<<<<< HEAD
=======

>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
import com.aditi.dripyard.config.JwtProvider;
import com.aditi.dripyard.exception.UserException;
import com.aditi.dripyard.model.User;
import com.aditi.dripyard.repository.PasswordResetTokenRepository;
import com.aditi.dripyard.repository.UserRepository;
<<<<<<< HEAD
import com.aditi.dripyard.service.MailerSendService;
import com.aditi.dripyard.service.UserService;
import lombok.RequiredArgsConstructor;
=======
import com.aditi.dripyard.service.UserService;
import org.springframework.mail.javamail.JavaMailSender;
>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
<<<<<<< HEAD
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
=======
public class UserServiceImplementation implements UserService {


	private UserRepository userRepository;
	private JwtProvider jwtProvider;
	private PasswordEncoder passwordEncoder;
	private PasswordResetTokenRepository passwordResetTokenRepository;
	private JavaMailSender javaMailSender;
	
	public UserServiceImplementation(
			UserRepository userRepository,
			JwtProvider jwtProvider,
			PasswordEncoder passwordEncoder,
			PasswordResetTokenRepository passwordResetTokenRepository,
			JavaMailSender javaMailSender) {
		
		this.userRepository=userRepository;
		this.jwtProvider=jwtProvider;
		this.passwordEncoder=passwordEncoder;
		this.passwordResetTokenRepository=passwordResetTokenRepository;
		this.javaMailSender=javaMailSender;
		
	}

	@Override
	public User findUserProfileByJwt(String jwt) throws UserException {
		String email=jwtProvider.getEmailFromJwtToken(jwt);
		
		
		User user = userRepository.findByEmail(email);
		
		if(user==null) {
			throw new UserException("user not exist with email "+email);
>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
		}
		return user;
	}

<<<<<<< HEAD
	@Override
	public User findUserByEmail(String username) throws UserException {
		User user = userRepository.findByEmail(username);

		if (user != null) {
			return user;
		}

		throw new UserException("user not exist with username " + username);
	}
}

=======


	
	@Override
	public User findUserByEmail(String username) throws UserException {
		
		User user=userRepository.findByEmail(username);
		
		if(user!=null) {
			
			return user;
		}
		
		throw new UserException("user not exist with username "+username);
	}



}
>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
