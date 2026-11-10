package com.grandcinema.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grandcinema.entity.Room;
import com.grandcinema.repository.RoomRepository;

// Liste des salles, pour le formulaire des séances de l'administrateur
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

	private final RoomRepository roomRepository;

	public RoomController(RoomRepository roomRepository) {
		this.roomRepository = roomRepository;
	}

	@GetMapping
	public List<Room> getAll() {
		return roomRepository.findAllByOrderByName();
	}

}
