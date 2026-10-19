package com.grandcinema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grandcinema.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

	List<Reservation> findBySessionId(Integer sessionId);

}
