package com.grandcinema.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.grandcinema.entity.Reservation;
import com.grandcinema.entity.Room;
import com.grandcinema.entity.Seat;
import com.grandcinema.entity.Session;
import com.grandcinema.repository.ReservationRepository;
import com.grandcinema.repository.SeatRepository;
import com.grandcinema.repository.SessionRepository;

@ExtendWith(MockitoExtension.class)
class SeatSelectionServiceTest {

	@Mock
	private SessionRepository sessionRepository;

	@Mock
	private SeatRepository seatRepository;

	@Mock
	private ReservationRepository reservationRepository;

	@InjectMocks
	private SeatSelectionService seatSelectionService;

	private Session session;
	private Seat seat1;
	private Seat seat2;
	private Seat seat3;

	// Une séance demain dans une salle de 3 sièges, le siège 2 est déjà réservé
	@BeforeEach
	void setUp() {
		Room room = new Room();
		room.setId(1);

		seat1 = createSeat(1, room);
		seat2 = createSeat(2, room);
		seat3 = createSeat(3, room);

		session = new Session();
		session.setId(1);
		session.setRoom(room);
		session.setSessionDt(LocalDateTime.now().plusDays(1));
	}

	@Test
	void getAvailableSeatsReturnsOnlyFreeSeats() {
		mockSessionWithReservedSeat2();

		List<Seat> availableSeats = seatSelectionService.getAvailableSeats(1);

		assertEquals(List.of(seat1, seat3), availableSeats);
	}

	@Test
	void selectSeatsReturnsSelectedSeats() {
		mockSessionWithReservedSeat2();

		List<Seat> selectedSeats = seatSelectionService.selectSeats(1, 2, List.of(1, 3));

		assertEquals(List.of(seat1, seat3), selectedSeats);
	}

	@Test
	void selectSeatsRefusesReservedSeat() {
		mockSessionWithReservedSeat2();

		assertStatus(HttpStatus.CONFLICT, () -> seatSelectionService.selectSeats(1, 1, List.of(2)));
	}

	@Test
	void selectSeatsRefusesWrongSeatCount() {
		mockSessionWithReservedSeat2();

		assertStatus(HttpStatus.BAD_REQUEST, () -> seatSelectionService.selectSeats(1, 2, List.of(1)));
	}

	@Test
	void selectSeatsRefusesZeroSeat() {
		mockSessionWithReservedSeat2();

		assertStatus(HttpStatus.BAD_REQUEST, () -> seatSelectionService.selectSeats(1, 0, List.of()));
	}

	@Test
	void selectSeatsRefusesSameSeatTwice() {
		mockSessionWithReservedSeat2();

		assertStatus(HttpStatus.BAD_REQUEST, () -> seatSelectionService.selectSeats(1, 2, List.of(1, 1)));
	}

	@Test
	void selectSeatsRefusesUnknownSeat() {
		mockSessionWithReservedSeat2();

		assertStatus(HttpStatus.BAD_REQUEST, () -> seatSelectionService.selectSeats(1, 1, List.of(42)));
	}

	@Test
	void unknownSessionIsRefused() {
		when(sessionRepository.findById(99)).thenReturn(Optional.empty());

		assertStatus(HttpStatus.NOT_FOUND, () -> seatSelectionService.getAvailableSeats(99));
	}

	@Test
	void pastSessionIsRefused() {
		session.setSessionDt(LocalDateTime.now().minusDays(1));
		when(sessionRepository.findById(1)).thenReturn(Optional.of(session));

		assertStatus(HttpStatus.CONFLICT, () -> seatSelectionService.getAvailableSeats(1));
	}

	// Les faux repositories renvoient la séance, ses 3 sièges et une réservation sur le siège 2
	private void mockSessionWithReservedSeat2() {
		Reservation reservation = new Reservation();
		reservation.setSeats(List.of(seat2));

		when(sessionRepository.findById(1)).thenReturn(Optional.of(session));
		when(seatRepository.findByRoomIdOrderBySeatNum(1)).thenReturn(List.of(seat1, seat2, seat3));
		when(reservationRepository.findBySessionId(1)).thenReturn(List.of(reservation));
	}

	private Seat createSeat(Integer id, Room room) {
		Seat seat = new Seat();
		seat.setId(id);
		seat.setRoom(room);
		seat.setSeatNum(id);
		return seat;
	}

	private void assertStatus(HttpStatus expectedStatus, Executable call) {
		ResponseStatusException exception = assertThrows(ResponseStatusException.class, call);
		assertEquals(expectedStatus, exception.getStatusCode());
	}

}
