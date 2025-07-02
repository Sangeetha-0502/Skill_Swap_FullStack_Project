
package com.example.skill_swap.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.skill_swap.Entities.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long>{
	Optional<Skill> findBySkillNameIgnoreCase(String skillName);


}
