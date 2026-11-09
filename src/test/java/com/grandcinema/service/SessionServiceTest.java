package com.grandcinema.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.grandcinema.dto.SessionRequest;
import com.grandcinema.entity.Movie;
import com.grandcinema.entity.Room;
import com.grandcinema.entity.Session;
import com.grandcinema.repository.MovieRepository;
import com.grandcinema.repository.RoomRepository;
import com.grandcinema.repository.SessionRepository;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

	private static final LocalDateTime DATE = LocalDateTime.of(2027, 2, 1, 20, 0);

	@Mock
	private SessionRepository sessionRepository;

	@Mock
	private MovieRepository movieRepository;

	@Mock
	private RoomRepository roomRepository;

	@InjectMocks
	private SessionService sessionService;

	@Test
	void getUpcomingSessionsIgnoresPastSessions() {
		Session pastSession = createSession(1, LocalDateTime.now().minusDays(1));
		Session tomorrowSession = createSession(2, LocalDateTime.now().plusDays(1));
		Session nextWeekSession = createSession(3, LocalDateTime.now().plusDays(7));
		when(sessionRepository.findAllByOrderBySessionDt()).thenReturn(List.of(pastSession, tomorrowSession, nextWeekSession));

		List<Session> sessions = sessionService.getUpcomingSessions();

		assertEquals(List.of(tomorrowSession, nextWeekSession), sessions);
	}

	@Test
	void createLinksMovieRoomAndDate() {
		Movie movie = new Movie();
		Room room = new Room();
		when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
		when(roomRepository.findById(2)).thenReturn(Optional.of(room));

		sessionService.create(new SessionRequest(1, 2, DATE));

		ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
		verify(sessionRepository).save(captor.capture());
		assertEquals(movie, captor.getValue().getMovie());
		assertEquals(room, captor.getValue().getRoom());
		assertEquals(DATE, captor.getValue().getSessionDt());
	}

	@Test
	void createRejectsMissingDate() {
		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> sessionService.create(new SessionRequest(1, 2, null)));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		verify(sessionRepository, never()).save(any());
	}

	@Test
	void createRejectsUnknownMovie() {
		when(movieRepository.findById(99)).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> sessionService.create(new SessionRequest(99, 2, DATE)));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
	}

	@Test
	void deleteRejectsUnknownSession() {
		when(sessionRepository.findById(99)).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> sessionService.delete(99));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
	}

	private Session createSession(Integer id, LocalDateTime sessionDt) {
		Session session = new Session();
		session.setId(id);
		session.setSessionDt(sessionDt);
		return session;
	}

}
