package com.grandcinema.dto;

import java.util.List;

// Statistiques de fréquentation renvoyées à l'administrateur : détail (séances, salles, horaires) et indicateurs globaux
public record Stats(List<SessionStats> sessions, List<GroupStats> rooms, List<GroupStats> hours, GroupStats total,
		long confirmedReservations, long cancelledReservations) {
}
