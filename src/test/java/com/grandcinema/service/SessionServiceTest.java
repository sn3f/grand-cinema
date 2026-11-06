package com.grandcinema.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.grandcinema.entity.Session;
import com.grandcinema.repository.SessionRepository;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

	@Mock
	private SessionRepository sessionRepository;

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

	private Session createSession(Integer id, LocalDateTime sessionDt) {
		Session session = new Session();
		session.setId(id);
		session.setSessionDt(sessionDt);
		return session;
	}

}
