
package com.example.skill_swap.skill_swap_project.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.skill_swap.skill_swap_project.Entities.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long>{
	Optional<Skill> findBySkillNameIgnoreCase(String skillName);


}
