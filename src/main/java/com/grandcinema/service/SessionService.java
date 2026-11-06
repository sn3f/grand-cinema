package com.grandcinema.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.grandcinema.entity.Session;
import com.grandcinema.repository.SessionRepository;

@Service
public class SessionService {

	private final SessionRepository sessionRepository;

	public SessionService(SessionRepository sessionRepository) {
		this.sessionRepository = sessionRepository;
	}

	// Retourne les séances à venir, triées par date (les séances passées sont ignorées)
	public List<Session> getUpcomingSessions() {
		LocalDateTime now = LocalDateTime.now();
		List<Session> upcomingSessions = new ArrayList<>();
		for (Session session : sessionRepository.findAllByOrderBySessionDt()) {
			if (session.getSessionDt().isAfter(now)) {
				upcomingSessions.add(session);
			}
		}
		return upcomingSessions;
	}

}
