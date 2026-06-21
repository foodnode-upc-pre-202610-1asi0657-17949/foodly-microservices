package com.foodly.business.application.service;

import com.foodly.business.application.dto.HuariqueCreateDto;
import com.foodly.business.application.dto.HuariqueProfileUpdateDto;
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

    public Optional<HuariqueResponseDto> getHuariqueByOwner(String ownerId) {
        return huariqueRepository.findByOwnerId(ownerId)
                .map(HuariqueResponseDto::new);
    }

    /**
     * Crea un nuevo huarique para el dueño autenticado.
     * Si el dueño ya tiene un local, no se crea uno nuevo (evita duplicados).
     */
    public Optional<HuariqueResponseDto> createHuarique(String ownerId, HuariqueCreateDto createDto) {
        if (huariqueRepository.findByOwnerId(ownerId).isPresent()) {
            return Optional.empty();
        }

        Huarique huarique = new Huarique();
        huarique.setOwnerId(ownerId);
        huarique.setName(createDto.getName());
        huarique.setAddress(createDto.getAddress());
        huarique.setCuisineType(createDto.getCuisineType());
        huarique.setPhone(createDto.getPhone());
        huarique.setPriceRange(createDto.getPriceRange());
        huarique.setLatitude(createDto.getLatitude());
        huarique.setLongitude(createDto.getLongitude());

        Huarique saved = huariqueRepository.save(huarique);
        return Optional.of(new HuariqueResponseDto(saved));
    }

    /**
     * Actualiza los datos de perfil (no el menú) del huarique del dueño autenticado.
     */
    public Optional<HuariqueResponseDto> updateProfile(String ownerId, HuariqueProfileUpdateDto updateDto) {
        Optional<Huarique> huariqueOpt = huariqueRepository.findByOwnerId(ownerId);
        if (huariqueOpt.isEmpty()) {
            return Optional.empty();
        }

        Huarique huarique = huariqueOpt.get();
        if (updateDto.getName() != null) huarique.setName(updateDto.getName());
        if (updateDto.getAddress() != null) huarique.setAddress(updateDto.getAddress());
        if (updateDto.getCuisineType() != null) huarique.setCuisineType(updateDto.getCuisineType());
        if (updateDto.getPhone() != null) huarique.setPhone(updateDto.getPhone());
        if (updateDto.getPriceRange() != null) huarique.setPriceRange(updateDto.getPriceRange());
        if (updateDto.getIsOpen() != null) huarique.setIsOpen(updateDto.getIsOpen());
        if (updateDto.getLatitude() != null) huarique.setLatitude(updateDto.getLatitude());
        if (updateDto.getLongitude() != null) huarique.setLongitude(updateDto.getLongitude());
        if (updateDto.getPhotos() != null) huarique.setPhotos(updateDto.getPhotos());
        if (updateDto.getSchedule() != null) huarique.setSchedule(updateDto.getSchedule());

        Huarique saved = huariqueRepository.save(huarique);
        return Optional.of(new HuariqueResponseDto(saved));
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