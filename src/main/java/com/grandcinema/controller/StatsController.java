package com.grandcinema.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

	@GetMapping
	public Stats getStats() {
		return statsService.getStats();
	}

}
