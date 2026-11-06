package com.grandcinema.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.grandcinema.entity.Reservation;
import com.grandcinema.entity.Seat;
import com.grandcinema.entity.Session;
import com.grandcinema.entity.User;
import com.grandcinema.repository.ReservationRepository;
import com.grandcinema.repository.SessionRepository;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

	@Mock
	private SeatSelectionService seatSelectionService;

	@Mock
	private UserService userService;

	@Mock
	private SessionRepository sessionRepository;

	@Mock
	private ReservationRepository reservationRepository;

	@InjectMocks
	private ReservationService reservationService;

	private Session session;
	private User user;
	private Seat seat1;
	private Seat seat3;

	// Jean veut réserver les sièges 1 et 3 de la séance 1, que la sélection de l'US03 accepte
	@BeforeEach
	void setUp() {
		session = new Session();
		session.setId(1);
		user = new User();
		user.setEmail("jb@gmail.com");
		seat1 = new Seat();
		seat1.setId(1);
		seat3 = new Seat();
		seat3.setId(3);

		when(seatSelectionService.selectSeats(1, 2, List.of(1, 3))).thenReturn(List.of(seat1, seat3));
		when(userService.getByEmail("jb@gmail.com")).thenReturn(user);
		when(sessionRepository.findById(1)).thenReturn(Optional.of(session));
	}

	@Test
	void confirmSavesConfirmedReservationForConnectedUser() {
		reservationService.confirm(1, List.of(1, 3), "jb@gmail.com");

		ArgumentCaptor<Reservation> savedReservation = ArgumentCaptor.forClass(Reservation.class);
		verify(reservationRepository).saveAndFlush(savedReservation.capture());
		Reservation reservation = savedReservation.getValue();

		assertEquals("CONFIRMED", reservation.getStatus());
		assertEquals(user, reservation.getUser());
		assertEquals(session, reservation.getSession());
		assertEquals(2, reservation.getReservationSeats().size());
		assertEquals(seat1, reservation.getReservationSeats().get(0).getSeat());
		assertEquals(seat3, reservation.getReservationSeats().get(1).getSeat());
	}

	// Deux clients valident en même temps : la base refuse la seconde réservation
	@Test
	void confirmReturnsConflictWhenSeatTakenAtTheSameTime() {
		when(reservationRepository.saveAndFlush(any(Reservation.class)))
				.thenThrow(new DataIntegrityViolationException("Duplicate entry for key session_id"));

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> reservationService.confirm(1, List.of(1, 3), "jb@gmail.com"));
		assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
	}

}
