package org.example.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto implements Serializable {

    @NotBlank(message = "Email is required")
    @Email(message = "Incorrect email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 4, max = 8, message = "Password must be between 4 and 8")
    private String password;
}
