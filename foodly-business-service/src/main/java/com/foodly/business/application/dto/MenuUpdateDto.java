package com.foodly.business.application.dto;

import com.foodly.business.domain.model.Menu;

public class MenuUpdateDto {
    private Menu menu;

    public MenuUpdateDto() {}

    public Menu getMenu() { return menu; }
    public void setMenu(Menu menu) { this.menu = menu; }
}
