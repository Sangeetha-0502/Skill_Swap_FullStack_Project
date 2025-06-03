package com.example.skill_swap.skill_swap_project.Services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.skill_swap.skill_swap_project.Entities.User;
import com.example.skill_swap.skill_swap_project.Entities.UserSkill;
import com.example.skill_swap.skill_swap_project.Enums.SkillType;
import com.example.skill_swap.skill_swap_project.Repositories.UserRepository;
import com.example.skill_swap.skill_swap_project.Repositories.UserSkillRepository;

@Service
public class SkillMatchService {

    @Autowired
    private UserSkillRepository userSkillRepository;

    @Autowired
    private UserRepository userRepository;

    public List<User> findMatchingUsers(Long userId) throws Exception {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        // 1. Get skills the user wants to learn
        List<UserSkill> learnSkills = userSkillRepository.findByUserAndType(currentUser, SkillType.LEARN);

        
        List<UserSkill> teachSkills = userSkillRepository.findByUserAndType(currentUser, SkillType.TEACH);

        List<User> matchingUsers = new ArrayList<>();

        for (UserSkill learnSkill : learnSkills) {
            // 3. Find other users who can teach the skill the current user wants to learn
            List<UserSkill> usersWhoCanTeach = userSkillRepository.findBySkillAndType(learnSkill.getSkill(), SkillType.TEACH);

            for (UserSkill userWhoCanTeach : usersWhoCanTeach) {
                User potentialMatch = userWhoCanTeach.getUser();
                if (potentialMatch.equals(currentUser)) continue;

                // 4. Check if that user also wants to learn something this user can teach
                for (UserSkill teachSkill : teachSkills) {
                    List<UserSkill> matchWantsToLearn = userSkillRepository.findByUserAndType(potentialMatch, SkillType.LEARN);
                    for (UserSkill learnFromMatch : matchWantsToLearn) {
                        if (learnFromMatch.getSkill().equals(teachSkill.getSkill())) {
                            matchingUsers.add(potentialMatch);
                            break;
                        }
                    }
                }
            }
        }

        return matchingUsers;
    }
}
