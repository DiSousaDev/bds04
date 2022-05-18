package com.devsuperior.bds04.dto;

import com.devsuperior.bds04.entities.User;

import javax.validation.constraints.Email;
import java.util.HashSet;
import java.util.Set;

public class UserDto {

    private Long id;
    private String password;

    @Email(message = "E-mail inválido.")
    private String email;

    private final Set<RoleDto> roles = new HashSet<>();

    public UserDto() {
    }

    public UserDto(Long id, String password, String email) {
        this.id = id;
        this.password = password;
        this.email = email;
    }

    public UserDto(User entity) {
        id = entity.getId();
        password = entity.getPassword();
        email = entity.getEmail();
        entity.getRoles().forEach(role -> this.roles.add(new RoleDto(role.getId(), role.getAuthority())));
    }

    public Long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Set<RoleDto> getRoles() {
        return roles;
    }
}
