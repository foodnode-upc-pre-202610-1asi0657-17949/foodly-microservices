package com.foodly.radar.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodly.radar.application.dto.NearbyRestaurantDto;
import com.foodly.radar.application.dto.RadarSearchRequestDto;
import com.foodly.radar.domain.model.GeoLocation;
import com.foodly.radar.infrastructure.geospatial.H3RadarService;
import com.foodly.radar.infrastructure.persistence.RedisRadarRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class GeoRadarServiceImpl implements GeoRadarService {

    @Inject
    private H3RadarService h3RadarService;

    @Inject
    private RedisRadarRepository redisRadarRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<NearbyRestaurantDto> searchNearbyRestaurants(RadarSearchRequestDto request) {
        GeoLocation userLocation = new GeoLocation(request.getLatitude(), request.getLongitude());

        String centerCell = h3RadarService.latLngToCell(userLocation);

        List<String> radarCells = h3RadarService.calculateRadarRing(centerCell, request.getKRingRadius());

        List<String> rawRestaurantsJson = redisRadarRepository.getRestaurantsFromCells(radarCells);

        List<NearbyRestaurantDto> nearbyRestaurants = new ArrayList<>();
        for (String json : rawRestaurantsJson) {
            try {
                NearbyRestaurantDto dto = objectMapper.readValue(json, NearbyRestaurantDto.class);
                nearbyRestaurants.add(dto);
            } catch (Exception e) {
                System.err.println("[RadarService] Error de mapeo de JSON en caché: " + e.getMessage());
            }
        }

        return nearbyRestaurants;
    }
}