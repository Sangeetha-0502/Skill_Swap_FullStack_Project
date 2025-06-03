package com.example.skill_swap.skill_swap_project.Services;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.skill_swap.skill_swap_project.Entities.Skill;
import com.example.skill_swap.skill_swap_project.Entities.SwapRequest;
import com.example.skill_swap.skill_swap_project.Entities.User;
import com.example.skill_swap.skill_swap_project.Enums.RequestStatus;
import com.example.skill_swap.skill_swap_project.Repositories.SkillRepository;
import com.example.skill_swap.skill_swap_project.Repositories.SwapRequestRepository;
import com.example.skill_swap.skill_swap_project.Repositories.UserRepository;

@Service
public class SwapRequestService {

	
	 @Autowired
	 private NotificationService notificationService;
	 
    @Autowired
    private SwapRequestRepository swaprequestrepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    SkillRepository skillrepository;

    public SwapRequest createRequest(Long senderId, Long receiverId, Long offeredSkillId, Long requestedSkillId) throws Exception {
        User sender = userRepository.findById(senderId).orElseThrow(() -> new Exception("Sender not found"));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new Exception("Receiver not found"));

        Skill offeredSkill = skillrepository.findById(offeredSkillId).orElseThrow(() -> new Exception("Offered Skill not found"));
        Skill requestedSkill = skillrepository.findById(requestedSkillId).orElseThrow(() -> new Exception("Requested Skill not found"));

        SwapRequest request = new SwapRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setOfferedSkill(offeredSkill);
        request.setRequestedSkill(requestedSkill);
        request.setStatus(RequestStatus.Pending);
        

        return swaprequestrepository.save(request);
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

