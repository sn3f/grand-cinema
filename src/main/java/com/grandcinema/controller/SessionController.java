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
import com.grandcinema.service.ReservationService;
import com.grandcinema.service.SeatSelectionService;

@RestController
@RequestMapping("/api/sessions/{sessionId}")
public class SessionController {

	private final SeatSelectionService seatSelectionService;
	private final ReservationService reservationService;

	public SessionController(SeatSelectionService seatSelectionService, ReservationService reservationService) {
		this.seatSelectionService = seatSelectionService;
		this.reservationService = reservationService;
	}

	@GetMapping("/seats")
	public List<Seat> getSeats(@PathVariable Integer sessionId) {
		return seatSelectionService.getSeats(sessionId);
	}

	@GetMapping("/available-seats")
	public List<Seat> getAvailableSeats(@PathVariable Integer sessionId) {
		return seatSelectionService.getAvailableSeats(sessionId);
	}

	@PostMapping("/selection")
	public List<Seat> selectSeats(@PathVariable Integer sessionId, @RequestParam Integer seatCount,
			@RequestParam List<Integer> seatIds) {
		return seatSelectionService.selectSeats(sessionId, seatCount, seatIds);
	}

	@PostMapping("/reservations")
	@ResponseStatus(HttpStatus.CREATED)
	public Reservation confirmReservation(@PathVariable Integer sessionId, @RequestParam List<Integer> seatIds,
			Principal principal) {
		return reservationService.confirm(sessionId, seatIds, principal.getName());
	}

}
