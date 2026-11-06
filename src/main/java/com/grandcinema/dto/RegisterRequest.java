package com.grandcinema.dto;

// Données envoyées par le client pour créer son compte (pas d'id ni de rôle : c'est l'API qui les choisit)
public record RegisterRequest(String firstName, String lastName, String email, String password) {
}
