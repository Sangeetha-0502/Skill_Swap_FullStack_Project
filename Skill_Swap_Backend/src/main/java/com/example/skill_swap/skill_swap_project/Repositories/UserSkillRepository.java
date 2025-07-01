package com.example.skill_swap.skill_swap_project.Repositories;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.skill_swap.skill_swap_project.Entities.Skill;
import com.example.skill_swap.skill_swap_project.Entities.User;
import com.example.skill_swap.skill_swap_project.Entities.UserSkill;
import com.example.skill_swap.skill_swap_project.Enums.SkillType;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

   
    // (Optional) Find all user skills for a user
    List<UserSkill> findByUserId(Long userId);

    // (Optional) Check if user has a particular skill of a specific type
    boolean existsByUserAndSkillAndType(User user, Skill skill, SkillType type);

	List<UserSkill> findByUserAndType(User currentUser, SkillType learn);

	List<UserSkill> findBySkillAndType(Skill skill, SkillType teach);

	List<UserSkill> findBySkillIdInAndType(Set<Long> myLearnIds, SkillType teach);

	Collection<User> findByUserIdAndType(Long id, SkillType teach);
	
	
	
}