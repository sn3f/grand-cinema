package com.grandcinema.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.grandcinema.entity.Movie;
import com.grandcinema.repository.MovieRepository;

@Service
public class MovieService {

	private final MovieRepository movieRepository;

	public MovieService(MovieRepository movieRepository) {
		this.movieRepository = movieRepository;
	}

	// Retourne tous les films, triés par titre
	public List<Movie> getAll() {
		return movieRepository.findAllByOrderByTitle();
	}

	// Crée un film après avoir vérifié son titre et sa durée
	public Movie create(Movie movie) {
		checkMovie(movie);
		movie.setId(null);
		return movieRepository.save(movie);
	}

	// Modifie le film existant avec les nouvelles informations
	public Movie update(Integer id, Movie movie) {
		checkExists(id);
		checkMovie(movie);
		movie.setId(id);
		return movieRepository.save(movie);
	}

	// Supprime le film : la base supprime aussi ses séances et leurs réservations (ON DELETE CASCADE)
	public void delete(Integer id) {
		checkExists(id);
		movieRepository.deleteById(id);
	}

	private void checkMovie(Movie movie) {
		if (movie.getTitle() == null || movie.getTitle().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le titre est obligatoire");
		}
		if (movie.getDuration() == null || movie.getDuration() <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La durée doit être supérieure à 0");
		}
	}

	private void checkExists(Integer id) {
		if (!movieRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film introuvable");
		}
	}

}
