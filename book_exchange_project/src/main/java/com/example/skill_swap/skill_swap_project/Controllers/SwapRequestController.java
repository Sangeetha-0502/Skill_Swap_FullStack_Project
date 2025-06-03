package com.example.skill_swap.skill_swap_project.Controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.skill_swap.skill_swap_project.Entities.SwapRequest;
import com.example.skill_swap.skill_swap_project.Enums.RequestStatus;
import com.example.skill_swap.skill_swap_project.Services.SwapRequestService;

@RestController
@RequestMapping("/api/swap-requests")
public class SwapRequestController {

    @Autowired
    private SwapRequestService requestService;

    @PostMapping("/create-swap-request")
    public SwapRequest createSwapRequest(@RequestParam Long senderId,
                                         @RequestParam Long receiverId,
                                         @RequestParam Long offeredSkillId,
                                         @RequestParam Long requestedSkillId) throws Exception {
        return requestService.createRequest(senderId, receiverId, offeredSkillId, requestedSkillId);
    }

    @PutMapping("/{id}/update-request-status")
    public SwapRequest updateRequestStatus(@PathVariable Long id, @RequestParam RequestStatus status) throws Exception {
        return requestService.updateStatus(id, status);
    }

    @GetMapping("/get-request-by-id/{id}")
    public SwapRequest getRequestById(@PathVariable Long id) throws Exception {
        return requestService.getRequestById(id).orElseThrow(() -> new Exception("Request not found"));
    }

    @DeleteMapping("/delete-request{id}")
    public void deleteRequest(@PathVariable Long id) throws Exception {
        requestService.deleteRequest(id);
    }
    
    // View sent requests
    @GetMapping("/sent/{userId}")
    public ResponseEntity<List<SwapRequest>> getSentRequests(@PathVariable Long userId) {
        List<SwapRequest> sentRequests = requestService.getSentRequests(userId);
        return new ResponseEntity<>(sentRequests, HttpStatus.OK);
    }

    // View received requests
    @GetMapping("/received/{userId}")
    public ResponseEntity<List<SwapRequest>> getReceivedRequests(@PathVariable Long userId) {
        List<SwapRequest> receivedRequests = requestService.getReceivedRequests(userId);
        return new ResponseEntity<>(receivedRequests, HttpStatus.OK);
    }
}
