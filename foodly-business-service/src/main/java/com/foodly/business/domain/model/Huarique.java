package com.foodly.business.domain.model;

public class Huarique {

    private String id;
    private String name;
    private String address;
    private String h3Index;
    private Double latitude;
    private Double longitude;
    private Menu menu;

    public Huarique() {
        this.menu = new Menu();
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
}