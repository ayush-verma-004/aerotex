package com.javnic.econe.service;


import com.javnic.econe.dto.profile.BusinessmanProfileDto;
import com.javnic.econe.dto.profile.FarmerProfileDto;
import com.javnic.econe.dto.profile.GovernmentProfileDto;
import com.javnic.econe.dto.profile.NGOProfileDto;

public interface ProfileService {
    FarmerProfileDto getFarmerProfile(String userId);
    FarmerProfileDto createFarmerProfile(String userId, FarmerProfileDto profileDto);
    FarmerProfileDto updateFarmerProfile(String userId, FarmerProfileDto profileDto);

    BusinessmanProfileDto getBusinessmanProfile(String userId);
    BusinessmanProfileDto createBusinessmanProfile(String userId, BusinessmanProfileDto profileDto);
    BusinessmanProfileDto updateBusinessmanProfile(String userId, BusinessmanProfileDto profileDto);

    NGOProfileDto getNGOProfile(String userId);
    NGOProfileDto updateNGOProfile(String userId, NGOProfileDto profileDto);

    GovernmentProfileDto getGovernmentProfile(String userId);
    GovernmentProfileDto updateGovernmentProfile(String userId, GovernmentProfileDto profileDto);
}
