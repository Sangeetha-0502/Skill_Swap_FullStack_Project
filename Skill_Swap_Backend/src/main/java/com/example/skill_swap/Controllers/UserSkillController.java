package com.example.skill_swap.Controllers;

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

import com.example.skill_swap.Entities.UserSkill;
import com.example.skill_swap.Enums.SkillType;
import com.example.skill_swap.Services.UserSkillServices;
import com.example.skill_swap.dtoClasses.UserSkillDto;

@RestController
@RequestMapping("/api/user-skills")
public class UserSkillController {

    @Autowired
    private UserSkillServices userskillservice;

    @PostMapping("/add-user-skill")
    public ResponseEntity<?> addUserSkill(@RequestBody UserSkillDto userskilldto) {
        try {
            UserSkill userSkill = userskillservice.addUserSkill(userskilldto);
            return ResponseEntity.ok(userSkill);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/update-user-skill-name/{userSkillId}")
    public ResponseEntity<?> updateUserSkillName(@PathVariable Long userSkillId,  @RequestParam String skillName){
    	try {
			UserSkill updatedUserSkill = userskillservice.updateUserSkillName(userSkillId, skillName);
			return ResponseEntity.ok(updatedUserSkill);
		} catch (Exception e) {
			 return ResponseEntity.status(401).body("user not found");
		}
    }

    @PutMapping("/update-user-skill-type/{userSkillId}")
    public ResponseEntity<?> updateUserSkill(@PathVariable Long userSkillId, @RequestParam SkillType type) {
    	System.out.println("Received Skill Name: " + type);
    	

        try {
            UserSkill updatedSkill = userskillservice.updateUserSkillType(userSkillId, type);
            return ResponseEntity.ok(updatedSkill);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/get-user-skills/{userId}")
    public ResponseEntity<?> getUserSkills(@PathVariable Long userId) {
        return ResponseEntity.ok(userskillservice.getUserSkills(userId));
    }
    
    @DeleteMapping("/delete-user-skill/{id}")
    public ResponseEntity<?> deleteUserSkill(@PathVariable Long id) {
 
        try {
            userskillservice.deleteUserSkill(id);
            return ResponseEntity.ok("UserSkill deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    

}
