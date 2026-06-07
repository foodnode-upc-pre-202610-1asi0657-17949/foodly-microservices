package com.foodly.community.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorite_huariques")
public class FavoriteHuarique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_username", nullable = false)
    private String customerUsername;

    @Column(name = "huarique_id", nullable = false)
    private String huariqueId;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    public FavoriteHuarique() {
        this.addedAt = LocalDateTime.now();
    }

    public FavoriteHuarique(String customerUsername, String huariqueId) {
        this.customerUsername = customerUsername;
        this.huariqueId = huariqueId;
        this.addedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerUsername() { return customerUsername; }
    public void setCustomerUsername(String customerUsername) { this.customerUsername = customerUsername; }

    public String getHuariqueId() { return huariqueId; }
    public void setHuariqueId(String huariqueId) { this.huariqueId = huariqueId; }

    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
}
