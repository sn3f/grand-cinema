package com.grandcinema.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.grandcinema.entity.Movie;
import com.grandcinema.entity.Reservation;
import com.grandcinema.entity.ReservationSeat;
import com.grandcinema.entity.Room;
import com.grandcinema.entity.Seat;
import com.grandcinema.entity.Session;
import com.grandcinema.entity.User;

@ExtendWith(MockitoExtension.class)
class TicketEmailServiceTest {

	@Mock
	private JavaMailSender mailSender;

	private TicketEmailService ticketEmailService;
	private Reservation reservation;

	// Réservation n°7 de Jean pour Inception, salle 1, sièges 2 et 3
	@BeforeEach
	void setUp() {
		ticketEmailService = new TicketEmailService(mailSender, "billetterie@grandcinema.com", "http://localhost:5173");

		User user = new User();
		user.setFirstName("Jean");
		user.setLastName("Bon");
		user.setEmail("jb@gmail.com");
		Movie movie = new Movie();
		movie.setTitle("Inception");
		Room room = new Room();
		room.setName("Salle 1");
		Session session = new Session();
		session.setMovie(movie);
		session.setRoom(room);
		session.setSessionDt(LocalDateTime.of(2027, 1, 15, 20, 0));

		Seat seat2 = new Seat();
		seat2.setSeatNum(2);
		Seat seat3 = new Seat();
		seat3.setSeatNum(3);
		ReservationSeat reservationSeat2 = new ReservationSeat();
		reservationSeat2.setSeat(seat2);
		ReservationSeat reservationSeat3 = new ReservationSeat();
		reservationSeat3.setSeat(seat3);

		reservation = new Reservation();
		reservation.setId(7);
		reservation.setUser(user);
		reservation.setSession(session);
		reservation.setReservationSeats(List.of(reservationSeat2, reservationSeat3));
	}

	@Test
	void sendTicketSendsEmailWithReservationDetails() {
		ticketEmailService.sendTicket(reservation);

		ArgumentCaptor<SimpleMailMessage> sentMessage = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mailSender).send(sentMessage.capture());
		SimpleMailMessage message = sentMessage.getValue();

		assertEquals("jb@gmail.com", message.getTo()[0]);
		assertTrue(message.getSubject().contains("n°7"));
		assertTrue(message.getText().contains("Jean Bon"));
		assertTrue(message.getText().contains("Inception"));
		assertTrue(message.getText().contains("15/01/2027 à 20:00"));
		assertTrue(message.getText().contains("Salle 1"));
		assertTrue(message.getText().contains("2, 3"));
		assertTrue(message.getText().contains("http://localhost:5173"));
	}

	// Serveur d'e-mail injoignable : pas d'erreur, la réservation reste confirmée
	@Test
	void sendTicketDoesNotFailWhenMailServerIsDown() {
		doThrow(new MailSendException("Connexion refusée")).when(mailSender).send(any(SimpleMailMessage.class));

		ticketEmailService.sendTicket(reservation);

		verify(mailSender).send(any(SimpleMailMessage.class));
	}

}
