package com.grandcinema.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.grandcinema.entity.Reservation;
import com.grandcinema.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

	private final ReservationService reservationService;

	public ReservationController(ReservationService reservationService) {
		this.reservationService = reservationService;
	}

	@GetMapping
	public List<Reservation> search(@RequestParam(required = false) String search) {
		return reservationService.search(search);
	}

	@GetMapping("/mine")
	public List<Reservation> getMyTickets(Principal principal) {
		return reservationService.getMyTickets(principal.getName());
	}

}
