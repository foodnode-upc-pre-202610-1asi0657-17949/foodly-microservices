package com.foodly.identity.domain.model;

/**
 * Roles disponibles en la plataforma Foodly.
 *
 * <ul>
 *   <li>{@code CLIENT}         – Comensal / explorador gastronómico.</li>
 *   <li>{@code DRIVER}         – Repartidor / logística.</li>
 *   <li>{@code HUARIQUE_ADMIN} – Dueño/administrador de un huarique.</li>
 *   <li>{@code PLATFORM_ADMIN} – Administrador interno de FoodNode.</li>
 * </ul>
 */
public enum UserRole {
    CLIENT,
    DRIVER,
    HUARIQUE_ADMIN,
    PLATFORM_ADMIN
}
