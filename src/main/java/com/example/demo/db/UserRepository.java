package com.example.demo.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserDTO, String> {

	List<UserDTO> findByID(String id);
	List<UserDTO> findByEmail(String email);
	List<UserDTO> findByName(String name);
	
}