package com.example.skill_swap.skill_swap_project.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.skill_swap.skill_swap_project.Entities.Skill;
import com.example.skill_swap.skill_swap_project.Entities.User;
import com.example.skill_swap.skill_swap_project.Entities.UserSkill;
import com.example.skill_swap.skill_swap_project.Enums.SkillType;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

    // Find all user skills by user and type (TEACH or LEARN)
    List<UserSkill> findByUserAndType(User user, SkillType type);

    // Find all users who have a specific skill and skill type
    List<UserSkill> findBySkillAndType(Skill skill, SkillType type);

    // (Optional) Find all user skills for a user
    List<UserSkill> findByUser(User user);

    // (Optional) Check if user has a particular skill of a specific type
    boolean existsByUserAndSkillAndType(User user, Skill skill, SkillType type);
}