package org.example.dto.user;

import lombok.Data;
import org.example.enums.Role;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private Role role;

}
