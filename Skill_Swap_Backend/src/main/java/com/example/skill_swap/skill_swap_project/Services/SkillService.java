package com.example.skill_swap.skill_swap_project.Services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.skill_swap.skill_swap_project.Entities.Skill;
import com.example.skill_swap.skill_swap_project.Repositories.SkillRepository;

@Service
public class SkillService {
	
	@Autowired
	private SkillRepository skillrepository;
	
	public Skill addSkill(Skill skill) {
		return skillrepository.save(skill);
	}
	
	public List<Skill> getAllSkills(){
		return skillrepository.findAll();
	}
	
	public Optional<Skill> getSkillById(Long id) {
		return skillrepository.findById(id);
	}
	
	public Skill updateSkill(Long id, Skill updatedSkill) throws Exception {
	    Skill existingSkill = skillrepository.findById(id)
	        .orElseThrow(() -> new Exception("Skill not found with id: " + id));

	    existingSkill.setSkillName(updatedSkill.getSkillName());
	    return skillrepository.save(existingSkill);
	}
	
	public void deleteSkill(Long id) {
        skillrepository.deleteById(id);
    }

	   public Map<String, Long> getSkillIdsByNames(String offeredName, String neededName) {
	        Skill requestedSkill = skillrepository.findBySkillNameIgnoreCase(offeredName)
	                .orElseThrow(() -> new RuntimeException("Offered skill not found: " + offeredName));

	        Skill offeredSkill = skillrepository.findBySkillNameIgnoreCase(neededName)
	                .orElseThrow(() -> new RuntimeException("Needed skill not found: " + neededName));

	        Map<String, Long> result = new HashMap<>();
	        result.put("requestedSkillId", requestedSkill.getId());
	        result.put("offeredSkillId", offeredSkill.getId());

	        return result;
	    }
}
