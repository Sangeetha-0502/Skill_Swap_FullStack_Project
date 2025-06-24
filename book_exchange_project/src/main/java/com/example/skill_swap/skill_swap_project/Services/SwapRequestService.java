package com.example.skill_swap.skill_swap_project.Services;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.skill_swap.skill_swap_project.Entities.Skill;
import com.example.skill_swap.skill_swap_project.Entities.SwapRequest;
import com.example.skill_swap.skill_swap_project.Entities.User;
import com.example.skill_swap.skill_swap_project.Enums.RequestStatus;
import com.example.skill_swap.skill_swap_project.Repositories.SkillRepository;
import com.example.skill_swap.skill_swap_project.Repositories.SwapRequestRepository;
import com.example.skill_swap.skill_swap_project.Repositories.UserRepository;
import com.example.skill_swap.skill_swap_project.dtoClasses.SwapRequestDto;

@Service
public class SwapRequestService {
	
	@Autowired
	private JavaMailSender mailSender;
	
	 @Autowired
	 private NotificationService notificationService;
	 
    @Autowired
    private SwapRequestRepository swaprequestrepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    SkillRepository skillrepository;

    public SwapRequest createRequest(SwapRequestDto dto) throws Exception {
    	User sender = userRepository.findById(dto.senderId()).orElseThrow(() -> new Exception("Sender not found"));
        User receiver = userRepository.findById(dto.receiverId()).orElseThrow(() -> new Exception("Receiver not found"));

        Skill requestedSkill = dto.requestedSkillId() != null ? 
        		skillrepository.findById(dto.requestedSkillId()).orElse(null) : null;

        Skill offeredSkill = dto.offeredSkillId() != null ?
        		skillrepository.findById(dto.offeredSkillId()).orElse(null) : null;

        SwapRequest req = new SwapRequest();
        req.setSender(sender);
        req.setReceiver(receiver);
        req.setRequestedSkill(requestedSkill);
        req.setOfferedSkill(offeredSkill);
        req.setNote(dto.note());
        req.setStatus(RequestStatus.Pending);
        System.out.println("saved successfully");
        swaprequestrepository.save(req);
       

        // Optional Email Notification
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(receiver.getEmail());
            message.setSubject("New Swap Request from " + sender.getName());
            message.setText("""
                    Hi %s,
                    
                    You’ve received a new Skill Swap request from %s.
                    
                    Message: %s
                    
                    Log in to your profile to accept or reject this request.
                    """.formatted(receiver.getName(), sender.getName(), dto.note()));
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Email failed: " + e.getMessage());
        }
        

        return swaprequestrepository.save(req);
    }

    public SwapRequest updateStatus(Long requestId, RequestStatus status) throws Exception {
        SwapRequest request = swaprequestrepository.findById(requestId)
            .orElseThrow(() -> new Exception("Request not found"));
        
        request.setStatus(status);
        
        if(status == RequestStatus.Accepted) {
            
            notificationService.sendNotification(request.getSender().getId(), 
                "Your swap request has been accepted by " + request.getReceiver().getName());
            notificationService.sendNotification(request.getReceiver().getId(), 
                "You accepted a swap request from " + request.getSender().getName());
            
        } else if(status == RequestStatus.Rejected) {
         
            notificationService.sendNotification(request.getSender().getId(), 
                "Your swap request has been rejected by " + request.getReceiver().getName());
        }
        
        return swaprequestrepository.save(request);
    }
    
    public Optional<SwapRequest> getRequestById(Long requestId) {
        return swaprequestrepository.findById(requestId);
    }

    public void deleteRequest(Long requestId) throws Exception {
        if (!swaprequestrepository.existsById(requestId)) {
            throw new Exception("Request not found");
        }
        swaprequestrepository.deleteById(requestId);
    }
    
    public List<SwapRequest> getSentRequests(Long userId) {
        return swaprequestrepository.findBySenderId(userId);
    }

    public List<SwapRequest> getReceivedRequests(Long userId) {
        return swaprequestrepository.findByReceiverId(userId);
    }

}

