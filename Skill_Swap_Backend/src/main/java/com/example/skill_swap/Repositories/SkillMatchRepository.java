package com.example.skill_swap.Repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.skill_swap.Entities.Skill;
import com.example.skill_swap.Entities.UserSkill;
import com.example.skill_swap.Enums.SkillType;

@Repository
public interface SkillMatchRepository extends JpaRepository<UserSkill, Long> {

	List<UserSkill> findByUserIdAndType(Long userId, SkillType type);

	List<UserSkill> findBySkillInAndType(Set<Skill> skills, SkillType type);

}
