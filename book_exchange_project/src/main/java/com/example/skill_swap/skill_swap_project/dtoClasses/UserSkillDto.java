package com.example.skill_swap.skill_swap_project.dtoClasses;

import com.example.skill_swap.skill_swap_project.Enums.SkillType;

public class UserSkillDto {
	
	    private Long userId;
	    private String skillName;
	    private SkillType type;
		public Long getUserId() {
			return userId;
		}
		public void setUserId(Long userId) {
			this.userId = userId;
		}
		public String getSkillName() {
			return skillName;
		}
		public void setSkillName(String skillName) {
			this.skillName = skillName;
		}
		public SkillType getType() {
			return type;
		}
		public void setType(SkillType type) {
			this.type = type;
		}
       

}
