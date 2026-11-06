package com.grandcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grandcinema.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

}
