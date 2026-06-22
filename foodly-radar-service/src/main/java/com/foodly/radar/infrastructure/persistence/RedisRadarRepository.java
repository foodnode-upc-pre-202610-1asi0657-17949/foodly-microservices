package com.foodly.radar.infrastructure.persistence;

import java.util.List;

public interface RedisRadarRepository {
    
    /**
     * Save serialized information of active restaurants in the hexagonal zone.
     */
    void saveToCell(String h3Index, String restaurantId, String restaurantJson);

    /**
     * Retrieve a list of serialized restaurants in the hexagonal zone.
     */
    List<String> getRestaurantsFromCells(List<String> h3Indexes);

    /**
     * Delete all restaurant information from the hexagonal zone.
     */
    void removeFromCell(String h3Index, String restaurantId);
}