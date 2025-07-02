package com.example.skill_swap.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.skill_swap.Services.SkillMatchService;
import com.example.skill_swap.dtoClasses.MatchDto;

@RestController
@RequestMapping("api/skill-matching")
public class SkillMatchController {

    @Autowired
    private SkillMatchService skillMatchingService;

    @GetMapping("/match/{userId}")
    public List<MatchDto> matchSkills(@PathVariable Long userId) throws Exception {
        return skillMatchingService.findMatchingUsers(userId);
    }
    
    @GetMapping("/search-user-name")
    public List<MatchDto> searchByName(@RequestParam String name) {
        return skillMatchingService.searchUsersWithSkills(name);
    }
}


