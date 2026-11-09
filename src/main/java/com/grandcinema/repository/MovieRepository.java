package com.grandcinema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grandcinema.entity.Movie;

public interface MovieRepository extends JpaRepository<Movie, Integer> {

	List<Movie> findAllByOrderByTitle();

}
