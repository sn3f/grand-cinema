package com.grandcinema.dto;

import java.time.LocalDateTime;

// Données envoyées par l'administrateur pour créer ou modifier une séance
public record SessionRequest(Integer movieId, Integer roomId, LocalDateTime sessionDt) {
}
