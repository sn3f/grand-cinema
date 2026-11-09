package com.grandcinema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grandcinema.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Integer> {

	List<Room> findAllByOrderByName();

}
