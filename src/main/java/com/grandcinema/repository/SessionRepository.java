package com.grandcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grandcinema.entity.Session;

public interface SessionRepository extends JpaRepository<Session, Integer> {

}
