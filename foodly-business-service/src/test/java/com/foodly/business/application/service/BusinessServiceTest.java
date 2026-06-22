package com.foodly.business.application.service;

import com.foodly.business.application.dto.HuariqueCreateDto;
import com.foodly.business.application.dto.HuariqueProfileUpdateDto;
import com.foodly.business.application.dto.HuariqueResponseDto;
import com.foodly.business.application.dto.MenuUpdateDto;
import com.foodly.business.application.dto.MenuUpdatedEventDto;
import com.foodly.business.domain.model.Huarique;
import com.foodly.business.domain.model.Menu;
import com.foodly.business.domain.model.Product;
import com.foodly.business.infrastructure.messaging.publisher.MenuEventPublisher;
import com.foodly.business.infrastructure.persistence.MongoHuariqueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BusinessServiceTest {

    @Mock
    private MongoHuariqueRepository mongoHuariqueRepository;

    @Mock
    private MenuEventPublisher menuEventPublisher;

    @InjectMocks
    private BusinessService businessService;

    // ==========================================
    // PRUEBAS PARA: createHuarique()
    // ==========================================

    @Test
    void createHuarique_Success_WhenOwnerHasNoHuarique() {
        // Arrange (Preparar datos)
        String ownerId = "owner123";
        HuariqueCreateDto createDto = new HuariqueCreateDto();
        createDto.setName("El Huarique de Fer");
        createDto.setAddress("Av. Aviación 1230, Lima");

        // Simulamos que el dueño NO tiene un local aún
        when(mongoHuariqueRepository.findByOwnerId(ownerId)).thenReturn(Optional.empty());

        // Simulamos el guardado retornando la entidad con un ID simulado
        when(mongoHuariqueRepository.save(any(Huarique.class))).thenAnswer(invocation -> {
            Huarique h = invocation.getArgument(0);
            h.setId("mocked-guid-123");
            return h;
        });

        // Act (Ejecutar el método bajo prueba)
        Optional<HuariqueResponseDto> result = businessService.createHuarique(ownerId, createDto);

        // Assert (Verificar resultados)
        assertTrue(result.isPresent());
        assertEquals("El Huarique de Fer", result.get().getName());
        assertEquals("mocked-guid-123", result.get().getId());

        verify(mongoHuariqueRepository, times(1)).save(any(Huarique.class));
    }

    @Test
    void createHuarique_ReturnsEmpty_WhenOwnerAlreadyHasHuarique() {
        // Arrange
        String ownerId = "ownerExisting";
        HuariqueCreateDto createDto = new HuariqueCreateDto();
        createDto.setName("Segundo Local");

        // Simulamos que el repositorio ya encuentra un huarique existente para este dueño
        Huarique existingHuarique = new Huarique();
        existingHuarique.setOwnerId(ownerId);
        when(mongoHuariqueRepository.findByOwnerId(ownerId)).thenReturn(Optional.of(existingHuarique));

        // Act
        Optional<HuariqueResponseDto> result = businessService.createHuarique(ownerId, createDto);

        // Assert
        assertTrue(result.isEmpty()); // Debe retornar Optional.empty() para evitar duplicados
        verify(mongoHuariqueRepository, never()).save(any(Huarique.class)); // Jamás debió guardar
    }

    // ==========================================
    // PRUEBAS PARA: updateMenu() y Eventos JMS
    // ==========================================

    @Test
    void updateMenu_Success_AndPublishesJmsEventWithCorrectProductCount() {
        // Arrange
        String huariqueId = "huarique999";
        Huarique existingHuarique = new Huarique();
        existingHuarique.setId(huariqueId);
        existingHuarique.setName("Huarique Logístico H3");

        when(mongoHuariqueRepository.findById(huariqueId)).thenReturn(Optional.of(existingHuarique));

        // Construimos un menú simulado con 2 productos para verificar la lógica de conteo
        Menu mockMenu = new Menu();
        Product p1 = new Product();
        Product p2 = new Product();
        mockMenu.setProducts(List.of(p1, p2));

        MenuUpdateDto updateDto = new MenuUpdateDto();
        updateDto.setMenu(mockMenu);

        // Act
        boolean isUpdated = businessService.updateMenu(huariqueId, updateDto);

        // Assert
        assertTrue(isUpdated);
        verify(mongoHuariqueRepository, times(1)).save(existingHuarique);

        // Capturador para verificar que el DTO del evento se construyó correctamente
        ArgumentCaptor<MenuUpdatedEventDto> eventCaptor = ArgumentCaptor.forClass(MenuUpdatedEventDto.class);
        verify(menuEventPublisher, times(1)).publishMenuUpdatedEvent(eventCaptor.capture());

        MenuUpdatedEventDto publishedEvent = eventCaptor.getValue();
        assertEquals(huariqueId, publishedEvent.getHuariqueId());
        assertEquals(2, publishedEvent.getTotalProducts()); // Valida que calculó bien el tamaño de la lista
    }

    @Test
    void updateMenu_ReturnsFalse_WhenHuariqueDoesNotExist() {
        // Arrange
        String invalidId = "non-existent-id";
        MenuUpdateDto updateDto = new MenuUpdateDto();

        when(mongoHuariqueRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act
        boolean isUpdated = businessService.updateMenu(invalidId, updateDto);

        // Assert
        assertFalse(isUpdated);
        verify(mongoHuariqueRepository, never()).save(any());
        verifyNoInteractions(menuEventPublisher); // No se debió publicar ningún evento JMS
    }

    // ==========================================
    // PRUEBAS PARA: updateProfile()
    // ==========================================

    @Test
    void updateProfile_UpdatesOnlyNonNullFields() {
        // Arrange
        String ownerId = "owner777";
        Huarique oldHuarique = new Huarique();
        oldHuarique.setOwnerId(ownerId);
        oldHuarique.setName("Nombre Antiguo");
        oldHuarique.setCuisineType("Marina");
        oldHuarique.setPhone("999999999");

        when(mongoHuariqueRepository.findByOwnerId(ownerId)).thenReturn(Optional.of(oldHuarique));
        when(mongoHuariqueRepository.save(any(Huarique.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Enviamos una actualización donde solo cambiamos el teléfono, el resto viene null
        HuariqueProfileUpdateDto updateDto = new HuariqueProfileUpdateDto();
        updateDto.setPhone("911111111");

        // Act
        Optional<HuariqueResponseDto> result = businessService.updateProfile(ownerId, updateDto);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("911111111", result.get().getPhone()); // Cambió
        assertEquals("Nombre Antiguo", result.get().getName()); // Se mantuvo intacto
        assertEquals("Marina", result.get().getCuisineType()); // Se mantuvo intacto
    }
}