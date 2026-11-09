package com.grandcinema.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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

import com.grandcinema.entity.Movie;
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

	@Mock
	private TicketEmailService ticketEmailService;

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
	}

	private void givenValidSelection() {
		when(seatSelectionService.selectSeats(1, 2, List.of(1, 3))).thenReturn(List.of(seat1, seat3));
		when(userService.getByEmail("jb@gmail.com")).thenReturn(user);
		when(sessionRepository.findById(1)).thenReturn(Optional.of(session));
	}

	@Test
	void confirmSavesConfirmedReservationForConnectedUser() {
		givenValidSelection();
		when(reservationRepository.saveAndFlush(any(Reservation.class))).thenReturn(new Reservation());

		Reservation result = reservationService.confirm(1, List.of(1, 3), "jb@gmail.com");

		ArgumentCaptor<Reservation> savedReservation = ArgumentCaptor.forClass(Reservation.class);
		verify(reservationRepository).saveAndFlush(savedReservation.capture());
		Reservation reservation = savedReservation.getValue();

		assertEquals("CONFIRMED", reservation.getStatus());
		assertEquals(user, reservation.getUser());
		assertEquals(session, reservation.getSession());
		assertEquals(2, reservation.getReservationSeats().size());
		assertEquals(seat1, reservation.getReservationSeats().get(0).getSeat());
		assertEquals(seat3, reservation.getReservationSeats().get(1).getSeat());
		assertEquals(36, reservation.getTicketCode().length());

		// Le billet est envoyé par e-mail pour la réservation enregistrée
		verify(ticketEmailService).sendTicket(result);
	}

	// Deux clients valident en même temps : la base refuse la seconde réservation
	@Test
	void confirmReturnsConflictWhenSeatTakenAtTheSameTime() {
		givenValidSelection();
		when(reservationRepository.saveAndFlush(any(Reservation.class)))
				.thenThrow(new DataIntegrityViolationException("Duplicate entry for key session_id"));

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> reservationService.confirm(1, List.of(1, 3), "jb@gmail.com"));
		assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
		verify(ticketEmailService, never()).sendTicket(any(Reservation.class));
	}

	// « Mes billets » : seulement les réservations confirmées de l'utilisateur connecté
	@Test
	void getMyTicketsReturnsConfirmedReservationsOfConnectedUser() {
		Reservation ticket = new Reservation();
		when(reservationRepository.findByUserEmailAndStatusOrderBySessionSessionDt("jb@gmail.com", "CONFIRMED"))
				.thenReturn(List.of(ticket));

		List<Reservation> tickets = reservationService.getMyTickets("jb@gmail.com");

		assertEquals(List.of(ticket), tickets);
	}

	// Recherche de l'administrateur : réservation n°1 de Jean Bernard pour Gladiator, n°2 d'Alice Martin pour Interstellar
	private void givenTwoReservations() {
		Reservation gladiator = createReservation(1, "Jean", "Bernard", "jb@gmail.com", "Gladiator");
		Reservation interstellar = createReservation(2, "Alice", "Martin", "am@gmail.com", "Interstellar");
		when(reservationRepository.findAllByOrderByCreationDateDesc()).thenReturn(List.of(gladiator, interstellar));
	}

	@Test
	void searchWithEmptyTextReturnsAllReservations() {
		givenTwoReservations();

		assertEquals(2, reservationService.search("").size());
	}

	@Test
	void searchFindsByClientNameIgnoringCase() {
		givenTwoReservations();

		List<Reservation> found = reservationService.search("MARTIN");

		assertEquals(1, found.size());
		assertEquals(2, found.get(0).getId());
	}

	@Test
	void searchFindsByReservationNumberOrMovieTitle() {
		givenTwoReservations();

		assertEquals(1, reservationService.search("1").get(0).getId());
		assertEquals(2, reservationService.search("inter").get(0).getId());
	}

	private Reservation createReservation(Integer id, String firstName, String lastName, String email, String title) {
		User client = new User();
		client.setFirstName(firstName);
		client.setLastName(lastName);
		client.setEmail(email);
		Movie movie = new Movie();
		movie.setTitle(title);
		Session reservedSession = new Session();
		reservedSession.setMovie(movie);

		Reservation reservation = new Reservation();
		reservation.setId(id);
		reservation.setUser(client);
		reservation.setSession(reservedSession);
		return reservation;
	}

}
