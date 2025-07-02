package com.example.skill_swap.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.skill_swap.Entities.User;
import com.example.skill_swap.Entities.UserSkill;
import com.example.skill_swap.Enums.SkillType;
import com.example.skill_swap.Repositories.UserRepository;
import com.example.skill_swap.Repositories.UserSkillRepository;
import com.example.skill_swap.dtoClasses.MatchDto;

@Service
public class SkillMatchService {

    @Autowired
    private UserSkillRepository userSkillRepo;
    @Autowired
    private UserRepository     userRepo;

    public List<MatchDto> findMatchingUsers(Long userId) throws Exception {

        User me = userRepo.findById(userId)
                          .orElseThrow(() -> new Exception("User not found"));

        /* -------------------------------------------
         * Fetch my teach + learn skills just once
         * ------------------------------------------*/
        List<UserSkill> myTeachUS = userSkillRepo.findByUserAndType(me, SkillType.TEACH);
        List<UserSkill> myLearnUS = userSkillRepo.findByUserAndType(me, SkillType.LEARN);

        Set<Long> myTeachIds  = myTeachUS.stream()
                                         .map(us -> us.getSkill().getId())
                                         .collect(Collectors.toSet());
        Set<Long> myLearnIds  = myLearnUS.stream()
                                         .map(us -> us.getSkill().getId())
                                         .collect(Collectors.toSet());

        /* -------------------------------------------
         * Pull all other users who teach ANY skill I
         * want to learn – one DB hit
         * ------------------------------------------*/
        List<UserSkill> teachesWhatILearn =
                userSkillRepo.findBySkillIdInAndType(myLearnIds, SkillType.TEACH);

            Map<Long, List<UserSkill>> cacheLearn = new HashMap<>();
            List<MatchDto> matches = new ArrayList<>();

            for (UserSkill us : teachesWhatILearn) {
                User cand = us.getUser();
                if (cand.equals(me)) continue;

                // Fetch candidate's LEARN list once and cache
                List<UserSkill> candLearn = cacheLearn.computeIfAbsent(
                    cand.getId(),
                    id -> userSkillRepo.findByUserAndType(cand, SkillType.LEARN)
                );

                // Fetch candidate's TEACH list (this was the broken part)
                List<UserSkill> candTeach = userSkillRepo.findByUserAndType(cand, SkillType.TEACH);

                // Match 1: What they teach that I want to learn
                List<String> theyTeachMe = candTeach.stream()
                    .filter(u -> myLearnIds.contains(u.getSkill().getId()))
                    .map(u -> u.getSkill().getSkillName())
                    .toList();

                // Match 2: What I teach that they want to learn
                List<String> iTeachThem = candLearn.stream()
                    .filter(u -> myTeachIds.contains(u.getSkill().getId()))
                    .map(u -> u.getSkill().getSkillName())
                    .toList();

                if (!theyTeachMe.isEmpty() && !iTeachThem.isEmpty()) {
                    matches.add(new MatchDto(
                        cand.getId(),
                        cand.getName(),
                        iTeachThem,      // Skills I can teach them
                        theyTeachMe      // Skills they can teach me
                    ));
                }
            }

            return matches;
    }

    public List<MatchDto> searchUsersWithSkills(String namePart) {

        return userRepo.findByNameContainingIgnoreCase(namePart)
                       .stream()
                       .map((User u) -> {                    // 👈 explicit type

                           /* the user’s own TEACH skills */
                           List<String> teaches = userSkillRepo
                                   .findByUserAndType(u, SkillType.TEACH)
                                   .stream()
                                   .map(us -> us.getSkill().getSkillName())
                                   .toList();

                           /* the user’s own LEARN skills */
                           List<String> learns = userSkillRepo
                                   .findByUserAndType(u, SkillType.LEARN)
                                   .stream()
                                   .map(us -> us.getSkill().getSkillName())
                                   .toList();

                           /* build the card DTO */
                           return new MatchDto(
                                   u.getId(),
                                   u.getName(),
                                   teaches,   // matchingTeaches  → user’s teach skills
                                   learns     // matchingLearns   → user’s learn skills
                           );
                       })
                       .toList();   // .collect(Collectors.toList()) if < JDK‑16
    }

}
