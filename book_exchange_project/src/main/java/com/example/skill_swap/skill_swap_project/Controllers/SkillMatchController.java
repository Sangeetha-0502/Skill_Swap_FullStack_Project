package com.example.skill_swap.skill_swap_project.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.skill_swap.skill_swap_project.Entities.User;
import com.example.skill_swap.skill_swap_project.Services.SkillMatchService;

@RestController
@RequestMapping("api/skill-matching")
public class SkillMatchController {

    @Autowired
    private SkillMatchService skillMatchingService;

    @GetMapping("/match/{userId}")
    public List<User> matchSkills(@PathVariable Long userId) throws Exception {
        return skillMatchingService.findMatchingUsers(userId);
    }
}


