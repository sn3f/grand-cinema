package com.grandcinema.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.grandcinema.dto.SessionRequest;
import com.grandcinema.entity.Movie;
import com.grandcinema.entity.Room;
import com.grandcinema.entity.Session;
import com.grandcinema.repository.MovieRepository;
import com.grandcinema.repository.RoomRepository;
import com.grandcinema.repository.SessionRepository;

@Service
public class SessionService {

	private final SessionRepository sessionRepository;
	private final MovieRepository movieRepository;
	private final RoomRepository roomRepository;

	public SessionService(SessionRepository sessionRepository, MovieRepository movieRepository,
			RoomRepository roomRepository) {
		this.sessionRepository = sessionRepository;
		this.movieRepository = movieRepository;
		this.roomRepository = roomRepository;
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

	// Crée une séance pour un film, une salle, une date et un horaire
	public Session create(SessionRequest request) {
		Session session = new Session();
		fill(session, request);
		return sessionRepository.save(session);
	}

	// Modifie le film, la salle ou l'horaire d'une séance existante
	public Session update(Integer id, SessionRequest request) {
		Session session = getById(id);
		fill(session, request);
		return sessionRepository.save(session);
	}

	// Supprime la séance : la base supprime aussi ses réservations (ON DELETE CASCADE)
	public void delete(Integer id) {
		Session session = getById(id);
		sessionRepository.delete(session);
	}

	private void fill(Session session, SessionRequest request) {
		if (request.movieId() == null || request.roomId() == null || request.sessionDt() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le film, la salle et la date sont obligatoires");
		}

		Optional<Movie> movie = movieRepository.findById(request.movieId());
		if (movie.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film introuvable");
		}
		Optional<Room> room = roomRepository.findById(request.roomId());
		if (room.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Salle introuvable");
		}

		session.setMovie(movie.get());
		session.setRoom(room.get());
		session.setSessionDt(request.sessionDt());
	}

	private Session getById(Integer id) {
		Optional<Session> result = sessionRepository.findById(id);
		if (result.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Séance introuvable");
		}
		return result.get();
	}

}
