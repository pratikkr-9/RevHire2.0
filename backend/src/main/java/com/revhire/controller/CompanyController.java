package com.revhire.controller;

import com.revhire.dto.CompanyDto;
import com.revhire.security.UserDetailsImpl;
import com.revhire.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @GetMapping
    public ResponseEntity<CompanyDto.CompanyResponse> getCompany(@AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(companyService.getCompany(user.getId()));
    }

    @PutMapping
    public ResponseEntity<CompanyDto.CompanyResponse> updateCompany(
            @RequestBody CompanyDto.UpdateCompanyRequest request,
            @AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(companyService.updateCompany(user.getId(), request));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<CompanyDto.CompanyResponse> getDashboard(@AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(companyService.getDashboard(user.getId()));
    }
}
