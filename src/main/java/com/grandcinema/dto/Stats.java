package com.grandcinema.dto;

import java.util.List;

// Statistiques de fréquentation renvoyées à l'administrateur
public record Stats(List<SessionStats> sessions, List<GroupStats> rooms, List<GroupStats> hours) {
}
