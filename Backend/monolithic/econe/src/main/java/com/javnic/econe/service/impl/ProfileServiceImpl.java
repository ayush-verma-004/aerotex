package com.javnic.econe.service.impl;

import com.javnic.econe.dto.profile.BusinessmanProfileDto;
import com.javnic.econe.dto.profile.FarmerProfileDto;
import com.javnic.econe.dto.profile.GovernmentProfileDto;
import com.javnic.econe.dto.profile.NGOProfileDto;
import com.javnic.econe.entity.*;
import com.javnic.econe.enums.UserRole;
import com.javnic.econe.exception.ResourceNotFoundException;
import com.javnic.econe.exception.UnauthorizedException;
import com.javnic.econe.exception.ValidationException;
import com.javnic.econe.mapper.ProfileMapper;
import com.javnic.econe.repository.*;
import com.javnic.econe.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final FarmerProfileRepository farmerProfileRepository;
    private final BusinessmanProfileRepository businessmanProfileRepository;
    private final NGOProfileRepository ngoProfileRepository;
    private final GovernmentProfileRepository governmentProfileRepository;
    private final ProfileMapper profileMapper;

    @Override
    public FarmerProfileDto getFarmerProfile(String userId) {
        validateUserRole(userId, UserRole.FARMER);
        FarmerProfile profile = farmerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer profile not found"));
        return profileMapper.toFarmerProfileDto(profile);
    }

    @Override
    @Transactional
    public FarmerProfileDto createFarmerProfile(String userId, FarmerProfileDto profileDto) {
        User user = validateUserRole(userId, UserRole.FARMER);

        // Check if profile already exists
        if (farmerProfileRepository.findByUserId(userId).isPresent()) {
            throw new ValidationException("Profile already exists. Use update instead.");
        }

        // Check Aadhar uniqueness
        if (farmerProfileRepository.existsByAadharNumber(profileDto.getAadharNumber())) {
            throw new ValidationException("Aadhar number already registered");
        }

        FarmerProfile profile = profileMapper.toFarmerProfile(profileDto);
        profile.setUserId(userId);
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());

        profile = farmerProfileRepository.save(profile);

        // Update user with profile ID
        user.setProfileId(profile.getId());
        userRepository.save(user);

        log.info("Farmer profile created for user: {}", userId);
        return profileMapper.toFarmerProfileDto(profile);
    }

    @Override
    @Transactional
    public FarmerProfileDto updateFarmerProfile(String userId, FarmerProfileDto profileDto) {
        validateUserRole(userId, UserRole.FARMER);

        FarmerProfile profile = farmerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        // Update fields
        profileMapper.updateFarmerProfile(profileDto, profile);
        profile.setUpdatedAt(LocalDateTime.now());

        profile = farmerProfileRepository.save(profile);

        log.info("Farmer profile updated for user: {}", userId);
        return profileMapper.toFarmerProfileDto(profile);
    }

    @Override
    public BusinessmanProfileDto getBusinessmanProfile(String userId) {
        validateUserRole(userId, UserRole.BUSINESSMAN);
        BusinessmanProfile profile = businessmanProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Businessman profile not found"));
        return profileMapper.toBusinessmanProfileDto(profile);
    }

    @Override
    @Transactional
    public BusinessmanProfileDto createBusinessmanProfile(String userId, BusinessmanProfileDto profileDto) {
        User user = validateUserRole(userId, UserRole.BUSINESSMAN);

        if (businessmanProfileRepository.findByUserId(userId).isPresent()) {
            throw new ValidationException("Profile already exists. Use update instead.");
        }

        // Validate GST and PAN uniqueness
        if (profileDto.getGstNumber() != null &&
                businessmanProfileRepository.existsByGstNumber(profileDto.getGstNumber())) {
            throw new ValidationException("GST number already registered");
        }

        if (profileDto.getPanNumber() != null &&
                businessmanProfileRepository.existsByPanNumber(profileDto.getPanNumber())) {
            throw new ValidationException("PAN number already registered");
        }

        BusinessmanProfile profile = profileMapper.toBusinessmanProfile(profileDto);
        profile.setUserId(userId);
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());

        profile = businessmanProfileRepository.save(profile);

        user.setProfileId(profile.getId());
        userRepository.save(user);

        log.info("Businessman profile created for user: {}", userId);
        return profileMapper.toBusinessmanProfileDto(profile);
    }

    @Override
    @Transactional
    public BusinessmanProfileDto updateBusinessmanProfile(String userId, BusinessmanProfileDto profileDto) {
        validateUserRole(userId, UserRole.BUSINESSMAN);

        BusinessmanProfile profile = businessmanProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        profileMapper.updateBusinessmanProfile(profileDto, profile);
        profile.setUpdatedAt(LocalDateTime.now());

        profile = businessmanProfileRepository.save(profile);

        log.info("Businessman profile updated for user: {}", userId);
        return profileMapper.toBusinessmanProfileDto(profile);
    }

    @Override
    public NGOProfileDto getNGOProfile(String userId) {
        validateUserRole(userId, UserRole.NGO);
        NGOProfile profile = ngoProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("NGO profile not found"));
        return profileMapper.toNGOProfileDto(profile);
    }

    @Override
    @Transactional
    public NGOProfileDto updateNGOProfile(String userId, NGOProfileDto profileDto) {
        validateUserRole(userId, UserRole.NGO);

        NGOProfile profile = ngoProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        profileMapper.updateNGOProfile(profileDto, profile);
        profile.setUpdatedAt(LocalDateTime.now());

        profile = ngoProfileRepository.save(profile);

        log.info("NGO profile updated for user: {}", userId);
        return profileMapper.toNGOProfileDto(profile);
    }

    @Override
    public GovernmentProfileDto getGovernmentProfile(String userId) {
        validateUserRole(userId, UserRole.GOVERNMENT);
        GovernmentProfile profile = governmentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Government profile not found"));
        return profileMapper.toGovernmentProfileDto(profile);
    }

    @Override
    @Transactional
    public GovernmentProfileDto updateGovernmentProfile(String userId, GovernmentProfileDto profileDto) {
        validateUserRole(userId, UserRole.GOVERNMENT);

        GovernmentProfile profile = governmentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        profileMapper.updateGovernmentProfile(profileDto, profile);
        profile.setUpdatedAt(LocalDateTime.now());

        profile = governmentProfileRepository.save(profile);

        log.info("Government profile updated for user: {}", userId);
        return profileMapper.toGovernmentProfileDto(profile);
    }

    private User validateUserRole(String userId, UserRole expectedRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != expectedRole) {
            throw new UnauthorizedException("User role mismatch");
        }

        return user;
    }
}