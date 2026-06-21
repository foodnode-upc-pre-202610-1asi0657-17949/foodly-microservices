package com.foodly.business.application.dto;

import jakarta.validation.constraints.NotBlank;

public class HuariqueCreateDto {

    @NotBlank(message = "El nombre del local es obligatorio")
    private String name;

    private String address;
    private String cuisineType;
    private String phone;
    private String priceRange;
    private Double latitude;
    private Double longitude;

    public HuariqueCreateDto() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCuisineType() { return cuisineType; }
    public void setCuisineType(String cuisineType) { this.cuisineType = cuisineType; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPriceRange() { return priceRange; }
    public void setPriceRange(String priceRange) { this.priceRange = priceRange; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}