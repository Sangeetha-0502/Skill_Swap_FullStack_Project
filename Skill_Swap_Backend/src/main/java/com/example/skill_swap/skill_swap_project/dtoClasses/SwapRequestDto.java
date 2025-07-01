package com.example.skill_swap.skill_swap_project.dtoClasses;

public record SwapRequestDto(Long senderId, Long receiverId, Long requestedSkillId, Long offeredSkillId, String note) {
}
