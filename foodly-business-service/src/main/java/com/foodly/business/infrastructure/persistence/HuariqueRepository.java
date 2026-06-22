package com.foodly.business.infrastructure.persistence;

import com.foodly.business.domain.model.Huarique;
import java.util.List;
import java.util.Optional;

public interface HuariqueRepository {
    List<Huarique> findAll();
    Optional<Huarique> findById(String id);
    Optional<Huarique> findByOwnerId(String ownerId);
    Huarique save(Huarique huarique);
}