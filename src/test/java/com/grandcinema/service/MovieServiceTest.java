package com.grandcinema.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.grandcinema.entity.Movie;
import com.grandcinema.repository.MovieRepository;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

	@Mock
	private MovieRepository movieRepository;

	@InjectMocks
	private MovieService movieService;

	@Test
	void createSavesValidMovie() {
		Movie movie = createMovie("Dune", 155);
		when(movieRepository.save(movie)).thenReturn(movie);

		Movie created = movieService.create(movie);

		assertEquals("Dune", created.getTitle());
		verify(movieRepository).save(movie);
	}

	@Test
	void createRejectsBlankTitle() {
		Movie movie = createMovie(" ", 155);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> movieService.create(movie));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		verify(movieRepository, never()).save(any());
	}

	@Test
	void updateRejectsUnknownMovie() {
		when(movieRepository.existsById(99)).thenReturn(false);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> movieService.update(99, createMovie("Dune", 155)));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
	}

	@Test
	void deleteRemovesExistingMovie() {
		when(movieRepository.existsById(1)).thenReturn(true);

		movieService.delete(1);

		verify(movieRepository).deleteById(1);
	}

	private Movie createMovie(String title, Integer duration) {
		Movie movie = new Movie();
		movie.setTitle(title);
		movie.setDuration(duration);
		return movie;
	}

}
