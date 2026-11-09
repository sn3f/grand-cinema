package com.grandcinema.dto;

import java.time.LocalDateTime;

// Fréquentation d'une séance : places réservées, capacité de la salle et taux de remplissage en %
public record SessionStats(Integer sessionId, String movieTitle, String roomName, LocalDateTime sessionDt,
		int bookedSeats, int totalSeats, int fillRate) {
}
