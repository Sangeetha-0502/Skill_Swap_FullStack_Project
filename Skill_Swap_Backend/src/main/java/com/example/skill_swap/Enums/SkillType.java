package com.example.skill_swap.Enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SkillType {
	TEACH, LEARN;
	
	@JsonCreator
    public static SkillType fromString(String key) {
        return key == null ? null : SkillType.valueOf(key);
    }
}

