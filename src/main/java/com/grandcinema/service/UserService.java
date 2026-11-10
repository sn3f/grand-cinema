package com.grandcinema.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.grandcinema.dto.RegisterRequest;
import com.grandcinema.entity.Role;
import com.grandcinema.entity.User;
import com.grandcinema.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	// Crée le compte d'un client : mot de passe hashé avec BCrypt et rôle USER imposé
	public User register(RegisterRequest request) {
		if (request.email() == null || request.email().isBlank() || request.password() == null || request.password().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'email et le mot de passe sont obligatoires");
		}
		if (userRepository.existsByEmail(request.email())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Un compte existe déjà avec cet email");
		}

		User user = new User();
		user.setFirstName(request.firstName());
		user.setLastName(request.lastName());
		user.setEmail(request.email());
		user.setPassword(passwordEncoder.encode(request.password()));
		user.setRole(Role.USER);
		return userRepository.save(user);
	}

	// Retourne l'utilisateur connecté à partir de son email
	public User getByEmail(String email) {
		Optional<User> result = userRepository.findByEmail(email);
		if (result.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable");
		}
		return result.get();
	}

	// Appelée par Spring Security à chaque requête authentifiée : charge l'utilisateur par son email
	@Override
	public UserDetails loadUserByUsername(String email) {
		Optional<User> result = userRepository.findByEmail(email);
		if (result.isEmpty()) {
			throw new UsernameNotFoundException("Utilisateur introuvable");
		}
		User user = result.get();
		return org.springframework.security.core.userdetails.User
				.withUsername(user.getEmail())
				.password(user.getPassword())
				.roles(user.getRole().name())
				.build();
	}

}
