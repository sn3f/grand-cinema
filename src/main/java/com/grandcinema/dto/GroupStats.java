package com.grandcinema.dto;

// Fréquentation cumulée d'un groupe de séances (une salle, un horaire...) : places réservées, places proposées et taux en %
public record GroupStats(String label, int bookedSeats, int totalSeats, int fillRate) {
}
