package com.grandcinema.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.grandcinema.dto.GroupStats;
import com.grandcinema.dto.SessionStats;
import com.grandcinema.dto.Stats;
import com.grandcinema.entity.Movie;
import com.grandcinema.entity.Reservation;
import com.grandcinema.entity.ReservationSeat;
import com.grandcinema.entity.Room;
import com.grandcinema.entity.Seat;
import com.grandcinema.entity.Session;
import com.grandcinema.repository.ReservationRepository;
import com.grandcinema.repository.ReservationSeatRepository;
import com.grandcinema.repository.SeatRepository;
import com.grandcinema.repository.SessionRepository;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

	@Mock
	private SessionRepository sessionRepository;

	@Mock
	private SeatRepository seatRepository;

	@Mock
	private ReservationSeatRepository reservationSeatRepository;

	@Mock
	private ReservationRepository reservationRepository;

	@InjectMocks
	private StatsService statsService;

	private Stats stats;

	// Salle 1 de 5 sièges : séance 1 à 14:00 avec 2 places confirmées + 1 place d'une réservation annulée,
	// séance 2 à 20:00 avec 5 places confirmées
	@BeforeEach
	void setUp() {
		Room room = new Room();
		room.setId(1);
		room.setName("Salle 1");
		Session afternoon = createSession(1, room, LocalDateTime.of(2026, 10, 3, 14, 0));
		Session evening = createSession(2, room, LocalDateTime.of(2026, 10, 3, 20, 0));

		when(sessionRepository.findAllByOrderBySessionDt()).thenReturn(List.of(afternoon, evening));
		when(seatRepository.findByRoomIdOrderBySeatNum(1)).thenReturn(createSeats(5));
		when(reservationSeatRepository.findBySessionId(1)).thenReturn(createReservationSeats(2, 1));
		when(reservationSeatRepository.findBySessionId(2)).thenReturn(createReservationSeats(5, 0));
		when(reservationRepository.countBySessionIdAndStatus(1, "CONFIRMED")).thenReturn(1L);
		when(reservationRepository.countBySessionIdAndStatus(1, "CANCELLED")).thenReturn(1L);
		when(reservationRepository.countBySessionIdAndStatus(2, "CONFIRMED")).thenReturn(1L);
		when(reservationRepository.countBySessionIdAndStatus(2, "CANCELLED")).thenReturn(0L);

		stats = statsService.getStats(null, null);
	}

	@Test
	void fillRateIsBasedOnRoomCapacityAndIgnoresCancelledReservations() {
		SessionStats afternoon = stats.sessions().get(0);

		assertEquals(2, afternoon.bookedSeats());
		assertEquals(5, afternoon.totalSeats());
		assertEquals(40, afternoon.fillRate());
		assertEquals(100, stats.sessions().get(1).fillRate());
	}

	@Test
	void roomsAddUpAllTheirSessions() {
		GroupStats room = stats.rooms().get(0);

		assertEquals("Salle 1", room.label());
		assertEquals(7, room.bookedSeats());
		assertEquals(10, room.totalSeats());
		assertEquals(70, room.fillRate());
	}

	@Test
	void hoursAreGroupedSeparately() {
		assertEquals(2, stats.hours().size());
		assertEquals("14:00", stats.hours().get(0).label());
		assertEquals(2, stats.hours().get(0).bookedSeats());
		assertEquals("20:00", stats.hours().get(1).label());
		assertEquals(5, stats.hours().get(1).bookedSeats());
	}

	// Indicateurs du tableau de bord (US12) : cumul de toutes les séances et nombre de réservations
	@Test
	void totalAddsUpAllSessionsAndCountsReservations() {
		assertEquals(7, stats.total().bookedSeats());
		assertEquals(10, stats.total().totalSeats());
		assertEquals(70, stats.total().fillRate());
		assertEquals(2, stats.confirmedReservations());
		assertEquals(1, stats.cancelledReservations());
	}

	// Analyse par période (US13) : le 4 octobre ne contient aucune séance, le 3 octobre contient les deux
	@Test
	void periodKeepsOnlySessionsBetweenTheTwoDates() {
		Stats emptyDay = statsService.getStats(LocalDate.of(2026, 10, 4), LocalDate.of(2026, 10, 4));
		Stats sessionDay = statsService.getStats(LocalDate.of(2026, 10, 3), LocalDate.of(2026, 10, 3));

		assertEquals(0, emptyDay.sessions().size());
		assertEquals(0, emptyDay.total().fillRate());
		assertEquals(2, sessionDay.sessions().size());
		assertEquals(70, sessionDay.total().fillRate());
	}

	private Session createSession(Integer id, Room room, LocalDateTime sessionDt) {
		Movie movie = new Movie();
		movie.setTitle("Gladiator");
		Session session = new Session();
		session.setId(id);
		session.setMovie(movie);
		session.setRoom(room);
		session.setSessionDt(sessionDt);
		return session;
	}

	private List<Seat> createSeats(int count) {
		List<Seat> seats = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			seats.add(new Seat());
		}
		return seats;
	}

	private List<ReservationSeat> createReservationSeats(int confirmed, int cancelled) {
		List<ReservationSeat> reservationSeats = new ArrayList<>();
		for (int i = 0; i < confirmed; i++) {
			reservationSeats.add(createReservationSeat("CONFIRMED"));
		}
		for (int i = 0; i < cancelled; i++) {
			reservationSeats.add(createReservationSeat("CANCELLED"));
		}
		return reservationSeats;
	}

	private ReservationSeat createReservationSeat(String status) {
		Reservation reservation = new Reservation();
		reservation.setStatus(status);
		ReservationSeat reservationSeat = new ReservationSeat();
		reservationSeat.setReservation(reservation);
		return reservationSeat;
	}

}
