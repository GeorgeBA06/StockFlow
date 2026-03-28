package org.example.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.enums.Role;

@Data
public class UserCreateDto {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 25,message = "Name must be between 2 and 25 characters")
    private String name;

    @NotBlank(message = "Password is required")
    @Size(min = 4, max = 8, message = "Password must be between 4 and 8")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Role is required")
    private Role role;
}
