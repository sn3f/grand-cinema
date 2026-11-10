package com.grandcinema.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.grandcinema.dto.MovieRequest;
import com.grandcinema.entity.Movie;
import com.grandcinema.repository.MovieRepository;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

	@Mock
	private MovieRepository movieRepository;

	@InjectMocks
	private MovieService movieService;

	@Test
	void getAllReturnsMoviesSortedByTitle() {
		Movie movie = new Movie();
		when(movieRepository.findAllByOrderByTitle()).thenReturn(List.of(movie));

		assertEquals(List.of(movie), movieService.getAll());
	}

	@Test
	void createSavesNewMovieWithRequestFields() {
		movieService.create(new MovieRequest("Dune", "SF", 155));

		ArgumentCaptor<Movie> savedMovie = ArgumentCaptor.forClass(Movie.class);
		verify(movieRepository).save(savedMovie.capture());
		assertNull(savedMovie.getValue().getId());
		assertEquals("Dune", savedMovie.getValue().getTitle());
		assertEquals("SF", savedMovie.getValue().getDescription());
		assertEquals(155, savedMovie.getValue().getDuration());
	}

	@Test
	void createRejectsBlankTitle() {
		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> movieService.create(new MovieRequest(" ", null, 155)));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		verify(movieRepository, never()).save(any());
	}

	@Test
	void createRejectsZeroDuration() {
		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> movieService.create(new MovieRequest("Dune", null, 0)));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		verify(movieRepository, never()).save(any());
	}

	@Test
	void updateSavesMovieWithItsId() {
		when(movieRepository.existsById(1)).thenReturn(true);

		movieService.update(1, new MovieRequest("Gladiator II", "Rome", 148));

		ArgumentCaptor<Movie> savedMovie = ArgumentCaptor.forClass(Movie.class);
		verify(movieRepository).save(savedMovie.capture());
		assertEquals(1, savedMovie.getValue().getId());
		assertEquals("Gladiator II", savedMovie.getValue().getTitle());
	}

	@Test
	void updateRejectsUnknownMovie() {
		when(movieRepository.existsById(99)).thenReturn(false);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> movieService.update(99, new MovieRequest("Dune", null, 155)));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
	}

	@Test
	void deleteRemovesExistingMovie() {
		when(movieRepository.existsById(1)).thenReturn(true);

		movieService.delete(1);

		verify(movieRepository).deleteById(1);
	}

	@Test
	void deleteRejectsUnknownMovie() {
		when(movieRepository.existsById(99)).thenReturn(false);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> movieService.delete(99));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		verify(movieRepository, never()).deleteById(any());
	}

}
