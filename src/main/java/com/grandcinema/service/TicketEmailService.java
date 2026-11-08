package com.grandcinema.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.grandcinema.entity.Reservation;
import com.grandcinema.entity.ReservationSeat;
import com.grandcinema.entity.Session;
import com.grandcinema.entity.User;

@Service
public class TicketEmailService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TicketEmailService.class);

	private final JavaMailSender mailSender;
	private final String from;

	public TicketEmailService(JavaMailSender mailSender, @Value("${mail.from}") String from) {
		this.mailSender = mailSender;
		this.from = from;
	}

	// Envoie le billet par e-mail ; si l'envoi échoue, la réservation reste confirmée
	public void sendTicket(Reservation reservation) {
		User user = reservation.getUser();
		Session session = reservation.getSession();

		List<String> seatNumbers = new ArrayList<>();
		for (ReservationSeat reservationSeat : reservation.getReservationSeats()) {
			seatNumbers.add(String.valueOf(reservationSeat.getSeat().getSeatNum()));
		}

		String date = session.getSessionDt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"));

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(user.getEmail());
		message.setSubject("Votre billet n°" + reservation.getId() + " - Grand Cinéma");
		message.setText("Bonjour " + user.getFirstName() + " " + user.getLastName() + ",\n\n"
				+ "Votre réservation est confirmée.\n\n"
				+ "Billet n°" + reservation.getId() + "\n"
				+ "Film : " + session.getMovie().getTitle() + "\n"
				+ "Séance : " + date + "\n"
				+ "Salle : " + session.getRoom().getName() + "\n"
				+ "Siège(s) : " + String.join(", ", seatNumbers) + "\n\n"
				+ "Présentez ce billet (ou son QR code dans l'application) à l'entrée du cinéma.");

		try {
			mailSender.send(message);
		} catch (MailException exception) {
			LOGGER.warn("E-mail du billet n°{} non envoyé : {}", reservation.getId(), exception.getMessage());
		}
	}

}
