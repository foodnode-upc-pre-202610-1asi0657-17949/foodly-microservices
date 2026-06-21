package com.foodly.business.application.dto;

import com.foodly.business.domain.model.DaySchedule;
import com.foodly.business.domain.model.Huarique;
import com.foodly.business.domain.model.Menu;

import java.util.List;

public class HuariqueResponseDto {
    private String id;
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

    public HuariqueResponseDto() {}

    public HuariqueResponseDto(Huarique huarique) {
        this.id = huarique.getId();
        this.name = huarique.getName();
        this.address = huarique.getAddress();
        this.h3Index = huarique.getH3Index();
        this.latitude = huarique.getLatitude();
        this.longitude = huarique.getLongitude();
        this.menu = huarique.getMenu();
        this.cuisineType = huarique.getCuisineType();
        this.phone = huarique.getPhone();
        this.priceRange = huarique.getPriceRange();
        this.isOpen = huarique.getIsOpen();
        this.photos = huarique.getPhotos();
        this.schedule = huarique.getSchedule();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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