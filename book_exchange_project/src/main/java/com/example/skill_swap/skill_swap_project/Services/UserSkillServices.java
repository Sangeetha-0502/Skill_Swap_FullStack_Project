package com.example.skill_swap.skill_swap_project.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.skill_swap.skill_swap_project.Entities.Skill;
import com.example.skill_swap.skill_swap_project.Entities.User;
import com.example.skill_swap.skill_swap_project.Entities.UserSkill;
import com.example.skill_swap.skill_swap_project.Enums.SkillType;
import com.example.skill_swap.skill_swap_project.Repositories.SkillRepository;
import com.example.skill_swap.skill_swap_project.Repositories.UserRepository;
import com.example.skill_swap.skill_swap_project.Repositories.UserSkillRepository;

@Service
public class UserSkillServices {

	@Autowired
	private UserRepository userrepository;

	@Autowired
	private UserSkillRepository userskillrepository;

	@Autowired
	private SkillRepository skillrepository;

	public UserSkill addUserSkill(Long userId, Long skillId, String skillName, SkillType skilltype) throws Exception {

		User user = userrepository.findById(userId).orElseThrow(() -> new Exception("User not found"));

		String normalizedSkillName = skillName.trim().toLowerCase();

		Skill skill = skillrepository.findBySkillNameIgnoreCase(normalizedSkillName).orElseGet(() -> {
			Skill newSkill = new Skill();
			newSkill.setSkillName(normalizedSkillName);
			return skillrepository.save(newSkill);
		});

		UserSkill userskill = new UserSkill();
		userskill.setUser(user);
		userskill.setSkill(skill);
		userskill.setType(skilltype);

		return userskillrepository.save(userskill);
	}
	
	public UserSkill UpdateUserSkillName(Long userSkillId, Long skillId, String skillName) throws Exception {
		
		UserSkill userskill = userskillrepository.findById(userSkillId).orElseThrow(() -> new Exception ("user not found"));
		Skill skill = skillrepository.findById(skillId).orElseGet(() -> {
		           Skill newskill = new Skill();
		           newskill.setSkillName(skillName);
		           return skillrepository.save(newskill);
		});
		
		userskill.setSkill(skill);
		
		return userskillrepository.save(userskill);
		
	}
	
	public UserSkill updateUserSkillType
	(Long userSkillId, SkillType newType) throws Exception {
	    UserSkill userSkill = userskillrepository.findById(userSkillId)
	        .orElseThrow(() -> new Exception("UserSkill not found"));

	    userSkill.setType(newType);
	    return userskillrepository.save(userSkill);
	}


	public void deleteUserSkill(Long id) throws Exception {
		if (!userskillrepository.existsById(id)) {
			throw new Exception("UserSkill not found");
		}
		userskillrepository.deleteById(id);
	}

	public Optional<UserSkill> getUserSkills(Long userId) {
		return userskillrepository.findById(userId);
	}

}
