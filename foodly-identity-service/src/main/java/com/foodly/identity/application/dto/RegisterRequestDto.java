package com.foodly.identity.application.dto;

import com.foodly.identity.domain.model.UserRole;
import jakarta.validation.constraints.*;

import java.util.Set;

public class RegisterRequestDto {

    @Email(message = "El email debe tener un formato válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$",
             message = "El username solo puede contener letras, números y guión bajo")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    private String password;

    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String firstName;

    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    private String lastName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$",
             message = "El número de teléfono debe estar en formato E.164")
    private String phoneNumber;

    private Set<UserRole> roles;

    public RegisterRequestDto() {}

    public String getEmail()                     { return email; }
    public void setEmail(String email)           { this.email = email; }

    public String getUsername()                        { return username; }
    public void setUsername(String username)           { this.username = username; }

    public String getPassword()                        { return password; }
    public void setPassword(String password)           { this.password = password; }

    public String getFirstName()                       { return firstName; }
    public void setFirstName(String firstName)         { this.firstName = firstName; }

    public String getLastName()                        { return lastName; }
    public void setLastName(String lastName)           { this.lastName = lastName; }

    public String getPhoneNumber()                     { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber)     { this.phoneNumber = phoneNumber; }

    public Set<UserRole> getRoles()                    { return roles; }
    public void setRoles(Set<UserRole> roles)          { this.roles = roles; }
}
