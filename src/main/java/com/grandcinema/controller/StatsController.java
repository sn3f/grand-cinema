package com.grandcinema.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.grandcinema.dto.Stats;
import com.grandcinema.service.StatsService;

// Statistiques de fréquentation, réservées à l'administrateur (voir SecurityConfig)
@RestController
@RequestMapping("/api/stats")
public class StatsController {

	private final StatsService statsService;

	public StatsController(StatsService statsService) {
		this.statsService = statsService;
	}

	// Période facultative : /api/stats?from=2026-10-22&to=2026-10-25
	@GetMapping
	public Stats getStats(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
		return statsService.getStats(from, to);
	}

}
