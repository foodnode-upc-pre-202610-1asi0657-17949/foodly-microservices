package com.foodly.radar.application.dto;

import jakarta.validation.constraints.NotNull;

public class RadarSearchRequestDto {

    @NotNull(message = "Latitude required")
    private Double latitude;

    @NotNull(message = "Longitude required")
    private Double longitude;

    private Integer kRingRadius = 1;

    public RadarSearchRequestDto() {}

    public RadarSearchRequestDto(Double latitude, Double longitude, Integer kRingRadius) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.kRingRadius = kRingRadius;
    }

    // Getters y Setters
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Integer getKRingRadius() { return kRingRadius; }
    public void setKRingRadius(Integer kRingRadius) { this.kRingRadius = kRingRadius; }
}