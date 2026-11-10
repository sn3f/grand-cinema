package com.grandcinema.dto;

// Données envoyées par l'administrateur pour créer ou modifier un film (pas d'id : c'est l'API qui le choisit)
public record MovieRequest(String title, String description, Integer duration) {
}
