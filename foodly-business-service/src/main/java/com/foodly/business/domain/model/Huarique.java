package com.foodly.business.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Huarique {

    private String id;
    private String ownerId;
    private String name;
    private String address;
    private String h3Index;
    private Double latitude;
    private Double longitude;
    private Menu menu;

    private String cuisineType;
    private String phone;
    private String priceRange;
    private Boolean isOpen;
    private List<String> photos;
    private List<DaySchedule> schedule;

    public Huarique() {
        this.menu = new Menu();
        this.photos = new ArrayList<>();
        this.schedule = new ArrayList<>();
        this.isOpen = true;
    }

    public Huarique(String id, String name, String address, String h3Index,
                    Double latitude, Double longitude, Menu menu) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.h3Index = h3Index;
        this.latitude = latitude;
        this.longitude = longitude;
        this.menu = menu != null ? menu : new Menu();
        this.photos = new ArrayList<>();
        this.schedule = new ArrayList<>();
        this.isOpen = true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getH3Index() { return h3Index; }
    public void setH3Index(String h3Index) { this.h3Index = h3Index; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Menu getMenu() { return menu; }
    public void setMenu(Menu menu) { this.menu = menu; }

    public String getCuisineType() { return cuisineType; }
    public void setCuisineType(String cuisineType) { this.cuisineType = cuisineType; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPriceRange() { return priceRange; }
    public void setPriceRange(String priceRange) { this.priceRange = priceRange; }

    public Boolean getIsOpen() { return isOpen; }
    public void setIsOpen(Boolean isOpen) { this.isOpen = isOpen; }

    public List<String> getPhotos() { return photos; }
    public void setPhotos(List<String> photos) { this.photos = photos; }

    public List<DaySchedule> getSchedule() { return schedule; }
    public void setSchedule(List<DaySchedule> schedule) { this.schedule = schedule; }
}