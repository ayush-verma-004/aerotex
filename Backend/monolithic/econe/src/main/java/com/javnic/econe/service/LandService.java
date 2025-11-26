package com.javnic.econe.service;

import com.javnic.econe.dto.land.request.CreateLandRequestDto;
import com.javnic.econe.entity.Land;

public interface LandService {

    Land createLand(CreateLandRequestDto createLandRequestDto);

}
