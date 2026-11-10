package com.grandcinema.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

	@Test
	void getByEmailReturnsUserOrNotFound() {
		User user = createUser();
		when(userRepository.findByEmail("jb@gmail.com")).thenReturn(Optional.of(user));
		when(userRepository.findByEmail("inconnu@test.com")).thenReturn(Optional.empty());

		assertEquals(user, userService.getByEmail("jb@gmail.com"));
		assertStatus(HttpStatus.NOT_FOUND, () -> userService.getByEmail("inconnu@test.com"));
	}

	// Connexion HTTP Basic : Spring Security charge l'utilisateur par son email, avec son hash et son rôle
	@Test
	void loadUserByUsernameGivesEmailPasswordHashAndRole() {
		when(userRepository.findByEmail("jb@gmail.com")).thenReturn(Optional.of(createUser()));

		UserDetails details = userService.loadUserByUsername("jb@gmail.com");

		assertEquals("jb@gmail.com", details.getUsername());
		assertEquals("hash", details.getPassword());
		assertEquals("ROLE_ADMIN", details.getAuthorities().iterator().next().getAuthority());
	}

	@Test
	void loadUserByUsernameRefusesUnknownEmail() {
		when(userRepository.findByEmail("inconnu@test.com")).thenReturn(Optional.empty());

		assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("inconnu@test.com"));
	}

	private User createUser() {
		User user = new User();
		user.setEmail("jb@gmail.com");
		user.setPassword("hash");
		user.setRole(Role.ADMIN);
		return user;
	}

	private void assertStatus(HttpStatus expectedStatus, Executable call) {
		ResponseStatusException exception = assertThrows(ResponseStatusException.class, call);
		assertEquals(expectedStatus, exception.getStatusCode());
	}

}
