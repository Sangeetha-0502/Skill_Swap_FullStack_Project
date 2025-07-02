package com.example.skill_swap.Repositories;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.skill_swap.Entities.User;
import com.example.skill_swap.dtoClasses.MatchDto;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	 
	
	
	Optional<User> findByEmail(String email);

	Collection<User> findByNameContainingIgnoreCase(String namePart);
	
	

	Optional<User> findByResetToken(String token);

}