package com.example.skill_swap.Services;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.skill_swap.Entities.Skill;
import com.example.skill_swap.Entities.SwapRequest;
import com.example.skill_swap.Entities.User;
import com.example.skill_swap.Enums.RequestStatus;
import com.example.skill_swap.Repositories.SkillRepository;
import com.example.skill_swap.Repositories.SwapRequestRepository;
import com.example.skill_swap.Repositories.UserRepository;
import com.example.skill_swap.dtoClasses.NotificationDto;
import com.example.skill_swap.dtoClasses.SwapRequestDto;

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

    public SwapRequest updateStatus(Long requestId, RequestStatus status) {
        SwapRequest request = swaprequestrepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(status);
        
        
       
            sendAcceptanceEmail(request);
        
        return  swaprequestrepository.save(request);
    }
    
    private void sendAcceptanceEmail(SwapRequest request) {
        String senderEmail = request.getSender().getEmail(); 
        String senderName = request.getSender().getName();
        String receiverName = request.getReceiver().getName();
        String offeredSkill = request.getOfferedSkill().getSkillName();
        String requestedSkill = request.getRequestedSkill().getSkillName();

        String subject;
        String body;

        if (request.getStatus() == RequestStatus.Accepted) {
            subject = "🎉 Your Skill Swap Request Was Accepted!";
            body = String.format(
                "Hi %s,\n\n%s has accepted your skill swap request!\n\n" +
                "🛠️ Offered Skill: %s\n" +
                "🎯 Requested Skill: %s\n\n" +
                "You're all set to connect and start swapping!\n\n" +
                "Regards,\nSkill Swap Team",
                senderName, receiverName, offeredSkill, requestedSkill
            );
        } else if (request.getStatus() == RequestStatus.Rejected) {
            subject = "Your Skill Swap Request Was Rejected!";
            body = String.format(
                "Hi %s,\n\n%s has rejected your skill swap request.\n\n" +
                "🛠️ Offered Skill: %s\n" +
                "🎯 Requested Skill: %s\n\n" +
                "We're sorry for the disappointment. Please update your profile and try again with another user.\n\n" +
                "Regards,\nSkill Swap Team",
                senderName, receiverName, offeredSkill, requestedSkill
            );
        } else {
            // if it's still pending or unknown, don't send an email
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(senderEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Email sending failed: " + e.getMessage());
        }
    }



        public List<NotificationDto> getAllNotificationsForUser(Long userId) {
            List<SwapRequest> swapRequests = swaprequestrepository.findAllByUserInvolved(userId);
            List<NotificationDto> notificationList = new ArrayList<>();

            for (SwapRequest sr : swapRequests) {
            	NotificationDto dto = new NotificationDto();

                dto.setRequestId(sr.getId());
                dto.setReceiverName(sr.getReceiver().getName());
                dto.setSenderName(sr.getSender().getName());
                dto.setOfferedSkill(sr.getOfferedSkill().getSkillName());
                dto.setRequestedSkill(sr.getRequestedSkill().getSkillName());
                dto.setNote(sr.getNote());
                dto.setStatus(sr.getStatus().toString()); 
                dto.setSenderId(sr.getSender().getId());       // ✅
                dto.setReceiverId(sr.getReceiver().getId()); 
                
                if (sr.getSender().getId().equals(userId)) {
                    dto.setUserRole("sender");
                } else {
                    dto.setUserRole("receiver");
                }
// enum to string

                notificationList.add(dto);
            }

            return notificationList;
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

