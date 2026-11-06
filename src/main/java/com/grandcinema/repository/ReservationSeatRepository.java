package com.grandcinema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grandcinema.entity.ReservationSeat;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Integer> {

	List<ReservationSeat> findBySessionId(Integer sessionId);

}
