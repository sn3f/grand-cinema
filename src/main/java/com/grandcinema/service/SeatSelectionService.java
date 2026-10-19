package com.grandcinema.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.grandcinema.entity.Reservation;
import com.grandcinema.entity.Seat;
import com.grandcinema.entity.Session;
import com.grandcinema.repository.ReservationRepository;
import com.grandcinema.repository.SeatRepository;
import com.grandcinema.repository.SessionRepository;

@Service
@Transactional(readOnly = true)
public class SeatSelectionService {

	private final SessionRepository sessionRepository;
	private final SeatRepository seatRepository;
	private final ReservationRepository reservationRepository;

	public SeatSelectionService(SessionRepository sessionRepository, SeatRepository seatRepository,
			ReservationRepository reservationRepository) {
		this.sessionRepository = sessionRepository;
		this.seatRepository = seatRepository;
		this.reservationRepository = reservationRepository;
	}

	// Retourne les sièges libres de la séance
	public List<Seat> getAvailableSeats(Integer sessionId) {
		Session session = findAvailableSession(sessionId);
		List<Integer> occupiedSeatIds = findOccupiedSeatIds(sessionId);
		List<Seat> roomSeats = seatRepository.findByRoomIdOrderBySeatNum(session.getRoom().getId());

		List<Seat> availableSeats = new ArrayList<>();
		for (Seat seat : roomSeats) {
			if (!occupiedSeatIds.contains(seat.getId())) {
				availableSeats.add(seat);
			}
		}
		return availableSeats;
	}

	// Vérifie la sélection du client et retourne les sièges choisis
	public List<Seat> selectSeats(Integer sessionId, Integer seatCount, List<Integer> seatIds) {
		Session session = findAvailableSession(sessionId);
		List<Integer> occupiedSeatIds = findOccupiedSeatIds(sessionId);
		List<Seat> roomSeats = seatRepository.findByRoomIdOrderBySeatNum(session.getRoom().getId());

		if (seatCount < 1) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nombre de places doit être au moins 1");
		}
		if (seatIds.size() != seatCount) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nombre de sièges sélectionnés doit correspondre au nombre de places");
		}

		List<Seat> selectedSeats = new ArrayList<>();
		for (Integer seatId : seatIds) {
			Seat seat = findSeat(roomSeats, seatId);
			if (seat == null) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le siège " + seatId + " n'existe pas dans la salle de cette séance");
			}
			if (selectedSeats.contains(seat)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un siège ne peut être sélectionné qu'une fois");
			}
			if (occupiedSeatIds.contains(seat.getId())) {
				throw new ResponseStatusException(HttpStatus.CONFLICT,
						"Le siège " + seat.getSeatNum() + " est déjà réservé");
			}
			selectedSeats.add(seat);
		}
		return selectedSeats;
	}

	// Une séance est disponible si elle existe et qu'elle n'est pas encore passée
	private Session findAvailableSession(Integer sessionId) {
		Session session = sessionRepository.findById(sessionId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Séance introuvable"));
		if (session.getSessionDt().isBefore(LocalDateTime.now())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "La séance est déjà passée");
		}
		return session;
	}

	// Un siège est occupé s'il fait partie d'une réservation de la séance
	private List<Integer> findOccupiedSeatIds(Integer sessionId) {
		List<Integer> occupiedSeatIds = new ArrayList<>();
		for (Reservation reservation : reservationRepository.findBySessionId(sessionId)) {
			for (Seat seat : reservation.getSeats()) {
				occupiedSeatIds.add(seat.getId());
			}
		}
		return occupiedSeatIds;
	}

	private Seat findSeat(List<Seat> seats, Integer seatId) {
		for (Seat seat : seats) {
			if (seat.getId().equals(seatId)) {
				return seat;
			}
		}
		return null;
	}

}
