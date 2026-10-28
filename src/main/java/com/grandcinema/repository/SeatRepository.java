package com.grandcinema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grandcinema.entity.Seat;

public interface SeatRepository extends JpaRepository<Seat, Integer> {

	List<Seat> findByRoomIdOrderBySeatNum(Integer roomId);

}
