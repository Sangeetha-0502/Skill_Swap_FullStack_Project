package com.example.skill_swap.skill_swap_project.Controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.skill_swap.skill_swap_project.Entities.Skill;
import com.example.skill_swap.skill_swap_project.Services.SkillService;

@RestController
@RequestMapping("api/skills")
public class SkillController {
	
	@Autowired
	private SkillService skillservice;
	
	
	@PostMapping("/add-skill")
	public ResponseEntity<?> addSkill(@RequestBody Skill skill){
		Skill savedSkill = skillservice.addSkill(skill);
		return ResponseEntity.ok(savedSkill);
	}
	
	
    @GetMapping("/get-skills-ids")
    public ResponseEntity<Map<String, Long>> getSkillIds(
            @RequestParam String requested,
            @RequestParam String offered) {

        Map<String, Long> ids = skillservice.getSkillIdsByNames(requested, offered);
        return ResponseEntity.ok(ids);
    }
	
	@GetMapping("/get-skills")
	public ResponseEntity<?> getAllSkills(){
		return ResponseEntity.ok(skillservice.getAllSkills());
	}
	
	@PutMapping("/update-skill/{id}")
	public ResponseEntity<?> updateSkill(@PathVariable Long id, @RequestBody Skill skill) {
	    try {
	        Skill updatedSkill = skillservice.updateSkill(id, skill);
	        return ResponseEntity.ok(updatedSkill);
	    } catch (Exception e) {
	        return ResponseEntity.notFound().build();
	    }
	}
	
	@GetMapping("/get-skill-by-id/{id}")
    public ResponseEntity<Optional<Skill>> getSkillById(@PathVariable Long id) {
        Optional<Skill> skill = skillservice.getSkillById(id);
        return (skill != null) ? ResponseEntity.ok(skill) : ResponseEntity.notFound().build();
    }
	
    @DeleteMapping("/delete-skill/{id}")
    public ResponseEntity<?> deleteSkill(@PathVariable Long id) {
        skillservice.deleteSkill(id);
        return  ResponseEntity.ok("Skill deleted");
    }

}
