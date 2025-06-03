package com.example.skill_swap.skill_swap_project.Entities;

import jakarta.persistence.Id; 


import com.example.skill_swap.skill_swap_project.Enums.SkillType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;


@Entity
public class UserSkill {
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Skill getSkill() {
		return skill;
	}

	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	public SkillType getType() {
		return type;
	}

	public void setType(SkillType type) {
		this.type = type;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Skill skill;

    @Enumerated(EnumType.STRING)
    private SkillType type; // TEACH or LEARN
}
