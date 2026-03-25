package org.example.dto.user;

import lombok.Data;
import org.example.enums.Role;

@Data
public class UserCreateDto {
    private String name;
    private String password;
    private String email;
    private Role role;
}
