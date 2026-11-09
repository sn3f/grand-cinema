package com.grandcinema.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.grandcinema.entity.Movie;
import com.grandcinema.service.MovieService;

// Gestion des films, réservée à l'administrateur (voir SecurityConfig)
@RestController
@RequestMapping("/api/movies")
public class MovieController {

	private final MovieService movieService;

	public MovieController(MovieService movieService) {
		this.movieService = movieService;
	}

	@GetMapping
	public List<Movie> getAll() {
		return movieService.getAll();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Movie create(@RequestBody Movie movie) {
		return movieService.create(movie);
	}

	@PutMapping("/{id}")
	public Movie update(@PathVariable Integer id, @RequestBody Movie movie) {
		return movieService.update(id, movie);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Integer id) {
		movieService.delete(id);
	}

}
