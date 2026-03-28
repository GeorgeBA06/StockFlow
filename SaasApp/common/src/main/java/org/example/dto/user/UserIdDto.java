package org.example.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserIdDto {

    @NotNull(message = "ID is required!")
    Long id;
}
