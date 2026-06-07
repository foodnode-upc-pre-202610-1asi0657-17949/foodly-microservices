package com.foodly.business.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Menu {

    private List<String> categories;
    private List<Product> products;

    public Menu() {
        this.categories = new ArrayList<>();
        this.products = new ArrayList<>();
    }

    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
}