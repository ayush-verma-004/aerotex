package com.javnic.econe.dto.land.response;

import com.javnic.econe.enums.LandStatus;
import lombok.Data;

@Data
public class CreateLandResponseDto {

    private double landArea;
    private String landAddress;
    private String soilType;
    private String geoCoordinates;
    private LandStatus landStatus;

}
