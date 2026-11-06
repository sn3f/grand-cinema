package com.grandcinema.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.grandcinema.entity.Reservation;
import com.grandcinema.entity.Seat;
import com.grandcinema.entity.Session;
import com.grandcinema.service.ReservationService;
import com.grandcinema.service.SeatSelectionService;
import com.grandcinema.service.SessionService;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

	private final SessionService sessionService;
	private final SeatSelectionService seatSelectionService;
	private final ReservationService reservationService;

	public SessionController(SessionService sessionService, SeatSelectionService seatSelectionService,
			ReservationService reservationService) {
		this.sessionService = sessionService;
		this.seatSelectionService = seatSelectionService;
		this.reservationService = reservationService;
	}

	@GetMapping
	public List<Session> getUpcomingSessions() {
		return sessionService.getUpcomingSessions();
	}

	@GetMapping("/{sessionId}/seats")
	public List<Seat> getSeats(@PathVariable Integer sessionId) {
		return seatSelectionService.getSeats(sessionId);
	}

	@GetMapping("/{sessionId}/available-seats")
	public List<Seat> getAvailableSeats(@PathVariable Integer sessionId) {
		return seatSelectionService.getAvailableSeats(sessionId);
	}

	@PostMapping("/{sessionId}/selection")
	public List<Seat> selectSeats(@PathVariable Integer sessionId, @RequestParam Integer seatCount,
			@RequestParam List<Integer> seatIds) {
		return seatSelectionService.selectSeats(sessionId, seatCount, seatIds);
	}

	@PostMapping("/{sessionId}/reservations")
	@ResponseStatus(HttpStatus.CREATED)
	public Reservation confirmReservation(@PathVariable Integer sessionId, @RequestParam List<Integer> seatIds,
			Principal principal) {
		return reservationService.confirm(sessionId, seatIds, principal.getName());
	}

}
