package com.foodly.business.domain.model;

import org.bson.types.ObjectId;

public class Product {

    private String id;
    private String name;
    private String description;
    private Double price;
    private Boolean available;
    private String imageUrl;

    public Product() {
        this.id = new ObjectId().toHexString();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}