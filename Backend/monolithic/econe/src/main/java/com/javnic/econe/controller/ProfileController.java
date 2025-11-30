package com.javnic.econe.controller;

import com.javnic.econe.dto.profile.BusinessmanProfileDto;
import com.javnic.econe.dto.profile.FarmerProfileDto;
import com.javnic.econe.dto.profile.GovernmentProfileDto;
import com.javnic.econe.dto.profile.NGOProfileDto;
import com.javnic.econe.entity.User;
import com.javnic.econe.enums.UserRole;
import com.javnic.econe.exception.UnauthorizedException;
import com.javnic.econe.security.SecurityUtils;
import com.javnic.econe.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final SecurityUtils securityUtils;

    // Farmer Profile Endpoints
    @GetMapping("/farmer")
    public ResponseEntity<FarmerProfileDto> getFarmerProfile() {
        User currentUser = securityUtils.getCurrentUser();
        validateRole(currentUser, UserRole.FARMER);

        FarmerProfileDto profile = profileService.getFarmerProfile(currentUser.getId());
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/farmer")
    public ResponseEntity<FarmerProfileDto> createFarmerProfile(
            @Valid @RequestBody FarmerProfileDto profileDto) {
        User currentUser = securityUtils.getCurrentUser();
        validateRole(currentUser, UserRole.FARMER);

        FarmerProfileDto profile = profileService.createFarmerProfile(currentUser.getId(), profileDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @PutMapping("/farmer")
    public ResponseEntity<FarmerProfileDto> updateFarmerProfile(
            @Valid @RequestBody FarmerProfileDto profileDto) {
        User currentUser = securityUtils.getCurrentUser();
        validateRole(currentUser, UserRole.FARMER);

        FarmerProfileDto profile = profileService.updateFarmerProfile(currentUser.getId(), profileDto);
        return ResponseEntity.ok(profile);
    }

    // Businessman Profile Endpoints
    @GetMapping("/businessman")
    public ResponseEntity<BusinessmanProfileDto> getBusinessmanProfile() {
        User currentUser = securityUtils.getCurrentUser();
        validateRole(currentUser, UserRole.BUSINESSMAN);

        BusinessmanProfileDto profile = profileService.getBusinessmanProfile(currentUser.getId());
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/businessman")
    public ResponseEntity<BusinessmanProfileDto> createBusinessmanProfile(
            @Valid @RequestBody BusinessmanProfileDto profileDto) {
        User currentUser = securityUtils.getCurrentUser();
        validateRole(currentUser, UserRole.BUSINESSMAN);

        BusinessmanProfileDto profile = profileService.createBusinessmanProfile(currentUser.getId(), profileDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @PutMapping("/businessman")
    public ResponseEntity<BusinessmanProfileDto> updateBusinessmanProfile(
            @Valid @RequestBody BusinessmanProfileDto profileDto) {
        User currentUser = securityUtils.getCurrentUser();
        validateRole(currentUser, UserRole.BUSINESSMAN);

        BusinessmanProfileDto profile = profileService.updateBusinessmanProfile(currentUser.getId(), profileDto);
        return ResponseEntity.ok(profile);
    }

    // NGO Profile Endpoints
    @GetMapping("/ngo")
    public ResponseEntity<NGOProfileDto> getNGOProfile() {
        User currentUser = securityUtils.getCurrentUser();
        validateRole(currentUser, UserRole.NGO);

        NGOProfileDto profile = profileService.getNGOProfile(currentUser.getId());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/ngo")
    public ResponseEntity<NGOProfileDto> updateNGOProfile(
            @Valid @RequestBody NGOProfileDto profileDto) {
        User currentUser = securityUtils.getCurrentUser();
        validateRole(currentUser, UserRole.NGO);

        NGOProfileDto profile = profileService.updateNGOProfile(currentUser.getId(), profileDto);
        return ResponseEntity.ok(profile);
    }

    // Government Profile Endpoints
    @GetMapping("/government")
    public ResponseEntity<GovernmentProfileDto> getGovernmentProfile() {
        User currentUser = securityUtils.getCurrentUser();
        validateRole(currentUser, UserRole.GOVERNMENT);

        GovernmentProfileDto profile = profileService.getGovernmentProfile(currentUser.getId());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/government")
    public ResponseEntity<GovernmentProfileDto> updateGovernmentProfile(
            @Valid @RequestBody GovernmentProfileDto profileDto) {
        User currentUser = securityUtils.getCurrentUser();
        validateRole(currentUser, UserRole.GOVERNMENT);

        GovernmentProfileDto profile = profileService.updateGovernmentProfile(currentUser.getId(), profileDto);
        return ResponseEntity.ok(profile);
    }

    private void validateRole(User user, UserRole expectedRole) {
        if (user.getRole() != expectedRole) {
            throw new UnauthorizedException("Access denied for this profile type");
        }
    }
}