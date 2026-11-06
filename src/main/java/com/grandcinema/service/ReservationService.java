package com.grandcinema.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.grandcinema.entity.Reservation;
import com.grandcinema.entity.ReservationSeat;
import com.grandcinema.entity.Seat;
import com.grandcinema.entity.Session;
import com.grandcinema.entity.User;
import com.grandcinema.repository.ReservationRepository;
import com.grandcinema.repository.SessionRepository;

@Service
public class ReservationService {

	private final SeatSelectionService seatSelectionService;
	private final UserService userService;
	private final SessionRepository sessionRepository;
	private final ReservationRepository reservationRepository;

	public ReservationService(SeatSelectionService seatSelectionService, UserService userService,
			SessionRepository sessionRepository, ReservationRepository reservationRepository) {
		this.seatSelectionService = seatSelectionService;
		this.userService = userService;
		this.sessionRepository = sessionRepository;
		this.reservationRepository = reservationRepository;
	}

	// Revérifie la sélection (règles de l'US03) puis enregistre la réservation de l'utilisateur connecté
	@Transactional
	public Reservation confirm(Integer sessionId, List<Integer> seatIds, String email) {
		List<Seat> seats = seatSelectionService.selectSeats(sessionId, seatIds.size(), seatIds);
		User user = userService.getByEmail(email);
		Session session = sessionRepository.findById(sessionId).get();

		Reservation reservation = new Reservation();
		reservation.setUser(user);
		reservation.setSession(session);
		reservation.setCreationDate(LocalDateTime.now());
		reservation.setStatus("CONFIRMED");

		List<ReservationSeat> reservationSeats = new ArrayList<>();
		for (Seat seat : seats) {
			ReservationSeat reservationSeat = new ReservationSeat();
			reservationSeat.setReservation(reservation);
			reservationSeat.setSession(session);
			reservationSeat.setSeat(seat);
			reservationSeats.add(reservationSeat);
		}
		reservation.setReservationSeats(reservationSeats);

		// Deux réservations simultanées : la contrainte UNIQUE(session_id, seat_id) refuse la seconde
		try {
			return reservationRepository.saveAndFlush(reservation);
		} catch (DataIntegrityViolationException exception) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Un des sièges vient d'être réservé");
		}
	}

}
