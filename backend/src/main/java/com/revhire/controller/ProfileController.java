package com.revhire.controller;

import com.revhire.dto.ProfileDto;
import com.revhire.security.UserDetailsImpl;
import com.revhire.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping
    public ResponseEntity<ProfileDto.ProfileResponse> getProfile(@AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(profileService.getProfile(user.getId()));
    }

    @PutMapping
    public ResponseEntity<ProfileDto.ProfileResponse> updateProfile(
            @RequestBody ProfileDto.UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(profileService.updateProfile(user.getId(), request));
    }

    @PostMapping("/resume")
    public ResponseEntity<Map<String, String>> uploadResume(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetailsImpl user) throws IOException {
        String filename = profileService.uploadResume(user.getId(), file);
        return ResponseEntity.ok(Map.of("message", "Resume uploaded", "filename", filename));
    }
}
