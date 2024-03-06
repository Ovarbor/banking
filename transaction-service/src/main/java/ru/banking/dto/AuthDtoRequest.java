package ru.banking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthDtoRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
