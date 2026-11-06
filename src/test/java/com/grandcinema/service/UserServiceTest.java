package com.grandcinema.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.grandcinema.dto.RegisterRequest;
import com.grandcinema.entity.Role;
import com.grandcinema.entity.User;
import com.grandcinema.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	private PasswordEncoder passwordEncoder;
	private UserService userService;

	// Faux repository, mais vrai encodeur BCrypt pour vérifier le hash du mot de passe
	@BeforeEach
	void setUp() {
		passwordEncoder = new BCryptPasswordEncoder();
		userService = new UserService(userRepository, passwordEncoder);
	}

	@Test
	void registerHashesPasswordAndForcesUserRole() {
		RegisterRequest request = new RegisterRequest("Marie", "Durand", "marie@test.com", "secret123");
		when(userRepository.existsByEmail("marie@test.com")).thenReturn(false);

		userService.register(request);

		// Récupère l'utilisateur passé à save() pour vérifier son contenu
		ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(savedUser.capture());
		User user = savedUser.getValue();

		assertEquals("marie@test.com", user.getEmail());
		assertEquals(Role.USER, user.getRole());
		assertNotEquals("secret123", user.getPassword());
		assertTrue(passwordEncoder.matches("secret123", user.getPassword()));
	}

	@Test
	void registerRefusesTakenEmail() {
		RegisterRequest request = new RegisterRequest("Jean", "Bernard", "jb@gmail.com", "secret123");
		when(userRepository.existsByEmail("jb@gmail.com")).thenReturn(true);

		assertStatus(HttpStatus.CONFLICT, () -> userService.register(request));
	}

	@Test
	void registerRefusesBlankPassword() {
		RegisterRequest request = new RegisterRequest("Marie", "Durand", "marie@test.com", "");

		assertStatus(HttpStatus.BAD_REQUEST, () -> userService.register(request));
	}

	private void assertStatus(HttpStatus expectedStatus, Executable call) {
		ResponseStatusException exception = assertThrows(ResponseStatusException.class, call);
		assertEquals(expectedStatus, exception.getStatusCode());
	}

}
