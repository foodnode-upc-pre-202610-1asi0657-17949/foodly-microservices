package com.foodly.business.application.dto;

import java.time.LocalDateTime;

public class MenuUpdatedEventDto {

    private String huariqueId;
    private int totalProducts;
    private String eventTimestamp;

    public MenuUpdatedEventDto() {}

    public MenuUpdatedEventDto(String huariqueId, int totalProducts) {
        this.huariqueId = huariqueId;
        this.totalProducts = totalProducts;
        this.eventTimestamp = LocalDateTime.now().toString();
    }

    public String getHuariqueId() { return huariqueId; }
    public void setHuariqueId(String huariqueId) { this.huariqueId = huariqueId; }

    public int getTotalProducts() { return totalProducts; }
    public void setTotalProducts(int totalProducts) { this.totalProducts = totalProducts; }

    public String getEventTimestamp() { return eventTimestamp; }
    public void setEventTimestamp(String eventTimestamp) { this.eventTimestamp = eventTimestamp; }
}
