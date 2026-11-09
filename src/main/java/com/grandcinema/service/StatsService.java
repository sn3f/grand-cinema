package com.grandcinema.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.grandcinema.dto.GroupStats;
import com.grandcinema.dto.SessionStats;
import com.grandcinema.dto.Stats;
import com.grandcinema.entity.ReservationSeat;
import com.grandcinema.entity.Session;
import com.grandcinema.repository.ReservationSeatRepository;
import com.grandcinema.repository.SeatRepository;
import com.grandcinema.repository.SessionRepository;

@Service
public class StatsService {

	private final SessionRepository sessionRepository;
	private final SeatRepository seatRepository;
	private final ReservationSeatRepository reservationSeatRepository;

	public StatsService(SessionRepository sessionRepository, SeatRepository seatRepository,
			ReservationSeatRepository reservationSeatRepository) {
		this.sessionRepository = sessionRepository;
		this.seatRepository = seatRepository;
		this.reservationSeatRepository = reservationSeatRepository;
	}

	// Taux de remplissage de chaque séance (places des réservations confirmées / capacité de la salle), puis cumul par salle et par horaire
	public Stats getStats() {
		List<SessionStats> sessions = new ArrayList<>();
		Map<String, Integer> bookedByRoom = new TreeMap<>();
		Map<String, Integer> totalByRoom = new TreeMap<>();
		Map<String, Integer> bookedByHour = new TreeMap<>();
		Map<String, Integer> totalByHour = new TreeMap<>();

		for (Session session : sessionRepository.findAllByOrderBySessionDt()) {
			int booked = countConfirmedSeats(session.getId());
			int total = seatRepository.findByRoomIdOrderBySeatNum(session.getRoom().getId()).size();
			String room = session.getRoom().getName();
			String hour = session.getSessionDt().toLocalTime().toString();

			sessions.add(new SessionStats(session.getId(), session.getMovie().getTitle(), room,
					session.getSessionDt(), booked, total, fillRate(booked, total)));
			add(bookedByRoom, room, booked);
			add(totalByRoom, room, total);
			add(bookedByHour, hour, booked);
			add(totalByHour, hour, total);
		}

		return new Stats(sessions, toGroups(bookedByRoom, totalByRoom), toGroups(bookedByHour, totalByHour));
	}

	private int countConfirmedSeats(Integer sessionId) {
		int count = 0;
		for (ReservationSeat reservationSeat : reservationSeatRepository.findBySessionId(sessionId)) {
			if ("CONFIRMED".equals(reservationSeat.getReservation().getStatus())) {
				count++;
			}
		}
		return count;
	}

	private int fillRate(int booked, int total) {
		if (total == 0) {
			return 0;
		}
		return Math.round(booked * 100f / total);
	}

	private void add(Map<String, Integer> map, String key, int value) {
		map.put(key, map.getOrDefault(key, 0) + value);
	}

	private List<GroupStats> toGroups(Map<String, Integer> booked, Map<String, Integer> total) {
		List<GroupStats> groups = new ArrayList<>();
		for (String label : booked.keySet()) {
			groups.add(new GroupStats(label, booked.get(label), total.get(label), fillRate(booked.get(label), total.get(label))));
		}
		return groups;
	}

}
