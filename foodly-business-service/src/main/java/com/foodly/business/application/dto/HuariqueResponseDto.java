package com.foodly.business.application.dto;

import com.foodly.business.domain.model.Huarique;
import com.foodly.business.domain.model.Menu;

public class HuariqueResponseDto {
    private String id;
    private String name;
    private String address;
    private String h3Index;
    private Menu menu;

    public HuariqueResponseDto() {}

    public HuariqueResponseDto(Huarique huarique) {
        this.id = huarique.getId();
        this.name = huarique.getName();
        this.address = huarique.getAddress();
        this.h3Index = huarique.getH3Index();
        this.menu = huarique.getMenu();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getH3Index() { return h3Index; }
    public void setH3Index(String h3Index) { this.h3Index = h3Index; }

    public Menu getMenu() { return menu; }
    public void setMenu(Menu menu) { this.menu = menu; }
}
