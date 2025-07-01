package com.example.skill_swap.skill_swap_project.dtoClasses;

import java.util.List;

public class MatchDto {
	
	        Long   userId;
	        String name;
	       
			public MatchDto(Long userId, String name, List<String> matchingTeaches, List<String> matchingLearns) {
	
				this.userId = userId;
				this.name = name;
				this.matchingTeaches = matchingTeaches;
				this.matchingLearns = matchingLearns;
			}
			public Long getUserId() {
				return userId;
			}
			public void setUserId(Long userId) {
				this.userId = userId;
			}
			public String getName() {
				return name;
			}
			public void setName(String name) {
				this.name = name;
			}
			public List<String> getMatchingTeaches() {
				return matchingTeaches;
			}
			public void setMatchingTeaches(List<String> matchingTeaches) {
				this.matchingTeaches = matchingTeaches;
			}
			public List<String> getMatchingLearns() {
				return matchingLearns;
			}
			public void setMatchingLearns(List<String> matchingLearns) {
				this.matchingLearns = matchingLearns;
			}
			List<String> matchingTeaches;  // I teach them
	        List<String> matchingLearns; // they teach me
	

}
