package com.foodly.radar.infrastructure.geospatial;

import com.foodly.radar.domain.model.GeoLocation;
import com.uber.h3core.H3Core;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.List;

@ApplicationScoped
public class H3RadarService {

    private H3Core h3;
    
    private static final int H3_RESOLUTION = 8;

    @PostConstruct
    public void init() {
        try {
            this.h3 = H3Core.newInstance();
        } catch (IOException e) {
            throw new RuntimeException("Error crítico al inicializar el motor de Uber H3 en WildFly", e);
        }
    }

    public String latLngToCell(GeoLocation location) {
        return h3.latLngToCellAddress(location.getLatitude(), location.getLongitude(), H3_RESOLUTION);
    }


    public List<String> calculateRadarRing(String centerCell, int kRingRadius) {
        return h3.gridDisk(centerCell, kRingRadius);
    }
}