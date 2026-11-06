package com.grandcinema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grandcinema.entity.Session;

public interface SessionRepository extends JpaRepository<Session, Integer> {

	List<Session> findAllByOrderBySessionDt();

}
