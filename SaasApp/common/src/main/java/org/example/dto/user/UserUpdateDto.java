package org.example.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.enums.Role;

@Data
public class UserUpdateDto {
    @Size(min = 2, max = 25, message = "Name must be between 2 and 25 characters")
    private String name;

    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 4, max = 8, message = "Password must be between 4 and 8 characters")
    private String password;

    private Role role;
}
