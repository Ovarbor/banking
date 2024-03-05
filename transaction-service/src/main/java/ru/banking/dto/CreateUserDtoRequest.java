package ru.banking.dto;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDtoRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String phone;

    @Email
    @NotBlank
    private String email;

    @NotNull
    @PositiveOrZero
    private BigDecimal balance;

    @NotNull
    @Past(message = "Birthdate cant be in future")
    private LocalDate birthday;
}
