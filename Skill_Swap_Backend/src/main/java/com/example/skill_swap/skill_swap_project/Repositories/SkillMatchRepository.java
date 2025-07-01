package com.example.skill_swap.skill_swap_project.Repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.skill_swap.skill_swap_project.Entities.Skill;
import com.example.skill_swap.skill_swap_project.Entities.UserSkill;
import com.example.skill_swap.skill_swap_project.Enums.SkillType;

@Repository
public interface SkillMatchRepository extends JpaRepository<UserSkill, Long> {

	List<UserSkill> findByUserIdAndType(Long userId, SkillType type);

	List<UserSkill> findBySkillInAndType(Set<Skill> skills, SkillType type);

}
