package com.grandcinema.service;

import java.time.LocalDate;
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
import com.grandcinema.repository.ReservationRepository;
import com.grandcinema.repository.ReservationSeatRepository;
import com.grandcinema.repository.SeatRepository;
import com.grandcinema.repository.SessionRepository;

@Service
public class StatsService {

	private final SessionRepository sessionRepository;
	private final SeatRepository seatRepository;
	private final ReservationSeatRepository reservationSeatRepository;
	private final ReservationRepository reservationRepository;

	public StatsService(SessionRepository sessionRepository, SeatRepository seatRepository,
			ReservationSeatRepository reservationSeatRepository, ReservationRepository reservationRepository) {
		this.sessionRepository = sessionRepository;
		this.seatRepository = seatRepository;
		this.reservationSeatRepository = reservationSeatRepository;
		this.reservationRepository = reservationRepository;
	}

	// Taux de remplissage de chaque séance (places des réservations confirmées / capacité de la salle),
	// cumul par salle, par horaire et au total, nombre de réservations confirmées et annulées.
	// Seules les séances entre from et to (inclus) sont prises en compte ; sans date, toutes les séances.
	public Stats getStats(LocalDate from, LocalDate to) {
		List<SessionStats> sessions = new ArrayList<>();
		int totalBooked = 0;
		int totalSeats = 0;
		long confirmedReservations = 0;
		long cancelledReservations = 0;
		Map<String, Integer> bookedByRoom = new TreeMap<>();
		Map<String, Integer> totalByRoom = new TreeMap<>();
		Map<String, Integer> bookedByHour = new TreeMap<>();
		Map<String, Integer> totalByHour = new TreeMap<>();

		for (Session session : sessionRepository.findAllByOrderBySessionDt()) {
			LocalDate day = session.getSessionDt().toLocalDate();
			if ((from != null && day.isBefore(from)) || (to != null && day.isAfter(to))) {
				continue;
			}

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
			totalBooked = totalBooked + booked;
			totalSeats = totalSeats + total;
			confirmedReservations = confirmedReservations + reservationRepository.countBySessionIdAndStatus(session.getId(), "CONFIRMED");
			cancelledReservations = cancelledReservations + reservationRepository.countBySessionIdAndStatus(session.getId(), "CANCELLED");
		}

		GroupStats globalStats = new GroupStats("Total", totalBooked, totalSeats, fillRate(totalBooked, totalSeats));
		return new Stats(sessions, toGroups(bookedByRoom, totalByRoom), toGroups(bookedByHour, totalByHour), globalStats,
				confirmedReservations, cancelledReservations);
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
