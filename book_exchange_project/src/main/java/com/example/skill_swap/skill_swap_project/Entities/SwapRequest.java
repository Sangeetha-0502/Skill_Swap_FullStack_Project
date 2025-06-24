package com.example.skill_swap.skill_swap_project.Entities;

import com.example.skill_swap.skill_swap_project.Enums.RequestStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class SwapRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User sender;

    @ManyToOne
    private User receiver;

    @ManyToOne
    private Skill requestedSkill;

    @ManyToOne
    private Skill offeredSkill;
    
    @Column(length = 1000)
    private String note;

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.Pending;

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Skill getRequestedSkill() {
        return requestedSkill;
    }

    public void setRequestedSkill(Skill requestedSkill) {
        this.requestedSkill = requestedSkill;
    }

    public Skill getOfferedSkill() {
        return offeredSkill;
    }

    public void setOfferedSkill(Skill offeredSkill) {
        this.offeredSkill = offeredSkill;
    }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }


    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
    
    
}
