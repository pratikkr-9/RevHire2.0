package com.revhire.controller;

import com.revhire.dto.ApplicationDto;
import com.revhire.security.UserDetailsImpl;
import com.revhire.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    // Seeker endpoints
    @PostMapping("/seeker/apply")
    public ResponseEntity<ApplicationDto.ApplicationResponse> apply(
            @RequestBody ApplicationDto.ApplyRequest request,
            @AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(applicationService.apply(user.getId(), request));
    }

    @GetMapping("/seeker/my-applications")
    public ResponseEntity<List<ApplicationDto.ApplicationResponse>> getMyApplications(
            @AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(applicationService.getSeekerApplications(user.getId()));
    }

    @PatchMapping("/seeker/{id}/withdraw")
    public ResponseEntity<Map<String, String>> withdraw(@PathVariable Long id,
                                                         @RequestBody(required = false) ApplicationDto.WithdrawRequest request,
                                                         @AuthenticationPrincipal UserDetailsImpl user) {
        String reason = request != null ? request.getReason() : null;
        applicationService.withdraw(user.getId(), id, reason);
        return ResponseEntity.ok(Map.of("message", "Application withdrawn"));
    }

    // Employer endpoints
    @GetMapping("/employer/job/{jobId}")
    public ResponseEntity<List<ApplicationDto.ApplicationResponse>> getJobApplications(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(applicationService.getJobApplications(user.getId(), jobId));
    }

    @PatchMapping("/employer/{id}/status")
    public ResponseEntity<ApplicationDto.ApplicationResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody ApplicationDto.StatusUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(applicationService.updateApplicationStatus(user.getId(), id, request));
    }
}
