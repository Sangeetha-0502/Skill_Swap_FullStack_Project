
package com.example.skill_swap.skill_swap_project.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.skill_swap.skill_swap_project.Entities.SwapRequest;

@Repository
public interface SwapRequestRepository extends JpaRepository<SwapRequest, Long> {
	
	  List<SwapRequest> findBySenderId(Long senderId);

	  List<SwapRequest> findByReceiverId(Long receiverId);

}
