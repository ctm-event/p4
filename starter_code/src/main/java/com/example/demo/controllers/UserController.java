package com.example.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private final UserRepository userRepository;
	private final CartRepository cartRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	public UserController(
			UserRepository userRepository,
			CartRepository cartRepository,
			BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.cartRepository = cartRepository;
		this.userRepository = userRepository;
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}

	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}

	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		logger.info("Create User", createUserRequest);
		String userPassword = createUserRequest.getPassword();
		if (userPassword.length() < 8) {
			logger.error("Create user {}: Password requires at least 8 characters.",
					createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}

		String userPasswordConfirm = createUserRequest.getConfirmPassword();
		if (!userPassword.equals(userPasswordConfirm)) {
			logger.error("Create user {}: Password confirm miss match", createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}

		try {
			User user = new User();
			user.setUsername(createUserRequest.getUsername());
			user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
			Cart cart = new Cart();
			cartRepository.save(cart);
			user.setCart(cart);
			userRepository.save(user);
			return ResponseEntity.ok(user);
		} catch (Exception e) {
			logger.info("Create user error: {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}

}
