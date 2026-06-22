package com.foodly.radar.application.service;

import com.foodly.radar.application.dto.NearbyRestaurantDto;
import com.foodly.radar.application.dto.RadarSearchRequestDto;
import java.util.List;

public interface GeoRadarService {
    
    List<NearbyRestaurantDto> searchNearbyRestaurants(RadarSearchRequestDto request);
}