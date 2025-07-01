package com.example.skill_swap.skill_swap_project.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.skill_swap.skill_swap_project.Entities.SwapRequest;
import com.example.skill_swap.skill_swap_project.Enums.RequestStatus;
import com.example.skill_swap.skill_swap_project.Services.SwapRequestService;
import com.example.skill_swap.skill_swap_project.dtoClasses.NotificationDto;
import com.example.skill_swap.skill_swap_project.dtoClasses.SwapRequestDto;

@RestController
@RequestMapping("/api/swap-requests")
public class SwapRequestController {

    @Autowired
    private SwapRequestService requestService;

    /* Send */
    @PostMapping("/send-swap-request")
    public ResponseEntity<?> sendRequest(@RequestBody SwapRequestDto dto) {
        try {
            requestService.createRequest(dto);
            return ResponseEntity.ok("Request sent");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /* Update status */
    @PutMapping("/update-request-status/{id}")
    public SwapRequest updateRequestStatus(@PathVariable Long id,
                                           @RequestParam("status") RequestStatus status) throws Exception {
        return requestService.updateStatus(id, status);
    }



	@GetMapping("/get-notifications/{userId}")
	public ResponseEntity<List<NotificationDto>> getNotifications(@PathVariable Long userId) {
		List<NotificationDto> notifications = requestService.getAllNotificationsForUser(userId);
		return ResponseEntity.ok(notifications);
	}
    

    /* Get single */
    @GetMapping("/get-request-by-id/{id}")
    public SwapRequest getRequestById(@PathVariable Long id) throws Exception {
        return requestService.getRequestById(id)
                             .orElseThrow(() -> new Exception("Request not found"));
    }

    /* Delete */
    @DeleteMapping("/delete-request/{id}")
    public void deleteRequest(@PathVariable Long id) throws Exception {
        requestService.deleteRequest(id);
    }

    /* Sent list */
    @GetMapping("/sent/{userId}")
    public ResponseEntity<List<SwapRequest>> getSentRequests(@PathVariable Long userId) {
        return ResponseEntity.ok(requestService.getSentRequests(userId));
    }

    /* Received list */
    @GetMapping("/received/{userId}")
    public ResponseEntity<List<SwapRequest>> getReceivedRequests(@PathVariable Long userId) {
        return ResponseEntity.ok(requestService.getReceivedRequests(userId));
    }
   
}
