package com.foodly.business.application.service;

import com.foodly.business.application.dto.HuariqueResponseDto;
import com.foodly.business.application.dto.MenuUpdateDto;
import com.foodly.business.application.dto.MenuUpdatedEventDto;
import com.foodly.business.domain.model.Huarique;
import com.foodly.business.infrastructure.messaging.publisher.MenuEventPublisher;
import com.foodly.business.infrastructure.persistence.HuariqueRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped // Cambio clave para WildFly
public class BusinessService {
    @Inject
    private HuariqueRepository huariqueRepository;

    @Inject
    private MenuEventPublisher menuEventPublisher;

    public List<HuariqueResponseDto> getAllHuariques() {
        return huariqueRepository.findAll()
                .stream()
                .map(HuariqueResponseDto::new)
                .collect(Collectors.toList());
    }

    public Optional<HuariqueResponseDto> getHuariqueMenu(String huariqueId) {
        return huariqueRepository.findById(huariqueId)
                .map(HuariqueResponseDto::new);
    }

    public boolean updateMenu(String huariqueId, MenuUpdateDto updateDto) {
        Optional<Huarique> huariqueOpt = huariqueRepository.findById(huariqueId);

        if (huariqueOpt.isEmpty()) {
            return false;
        }

        Huarique huarique = huariqueOpt.get();
        huarique.setMenu(updateDto.getMenu());

        huariqueRepository.save(huarique);

        int totalProducts = (updateDto.getMenu() != null && updateDto.getMenu().getProducts() != null)
                ? updateDto.getMenu().getProducts().size() : 0;

        MenuUpdatedEventDto eventDto = new MenuUpdatedEventDto(huariqueId, totalProducts);
        menuEventPublisher.publishMenuUpdatedEvent(eventDto);

        return true;
    }
}