package com.example.skill_swap.dtoClasses;

public record SwapRequestDto(Long senderId, Long receiverId, Long requestedSkillId, Long offeredSkillId, String note) {
}
