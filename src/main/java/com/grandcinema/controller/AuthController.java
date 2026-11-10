package com.grandcinema.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.grandcinema.dto.RegisterRequest;
import com.grandcinema.entity.User;
import com.grandcinema.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UserService userService;

	public AuthController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public User register(@RequestBody RegisterRequest request) {
		return userService.register(request);
	}

	// Le front l'appelle avec email + mot de passe pour vérifier la connexion (401 si faux)
	@GetMapping("/me")
	public User me(Principal principal) {
		return userService.getByEmail(principal.getName());
	}

}
