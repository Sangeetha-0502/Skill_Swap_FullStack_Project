package com.example.skill_swap.skill_swap_project.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.skill_swap.skill_swap_project.Entities.Skill;
import com.example.skill_swap.skill_swap_project.Entities.User;
import com.example.skill_swap.skill_swap_project.Entities.UserSkill;
import com.example.skill_swap.skill_swap_project.Enums.SkillType;
import com.example.skill_swap.skill_swap_project.Repositories.SkillRepository;
import com.example.skill_swap.skill_swap_project.Repositories.UserRepository;
import com.example.skill_swap.skill_swap_project.Repositories.UserSkillRepository;
import com.example.skill_swap.skill_swap_project.dtoClasses.UserSkillDto;

@Service
public class UserSkillServices {

	@Autowired
	private UserRepository userrepository;

	@Autowired
	private UserSkillRepository userskillrepository;

	@Autowired
	private SkillRepository skillrepository;

	public UserSkill addUserSkill(UserSkillDto userskilldto) throws Exception {

		User user = userrepository.findById(userskilldto.getUserId()).orElseThrow(() -> new Exception("User not found"));

		String normalizedSkillName = userskilldto.getSkillName().trim().toLowerCase();

		Skill skill = skillrepository.findBySkillNameIgnoreCase(normalizedSkillName).orElseGet(() -> {
			Skill newSkill = new Skill();
			newSkill.setSkillName(normalizedSkillName);
			return skillrepository.save(newSkill);
		});

		UserSkill userskill = new UserSkill();
		userskill.setUser(user);
		userskill.setSkill(skill);
		userskill.setType(userskilldto.getType());

		return userskillrepository.save(userskill);
	}
	
	public UserSkill updateUserSkillName(Long userSkillId, String skillName) throws Exception {

	    UserSkill userSkill = userskillrepository
	        .findById(userSkillId)
	        .orElseThrow(() -> new Exception("User‑skill link not found"));

	    Skill skill = skillrepository
	        .findBySkillNameIgnoreCase(skillName.trim())
	        .orElseGet(() -> {
	            Skill s = new Skill();
	            s.setSkillName(skillName.trim());
	            return skillrepository.save(s);
	        });

	    userSkill.setSkill(skill);
	    return userskillrepository.save(userSkill);
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

	public List<UserSkill> getUserSkills(Long userId) {
		return userskillrepository.findByUserId(userId);
	}

}
