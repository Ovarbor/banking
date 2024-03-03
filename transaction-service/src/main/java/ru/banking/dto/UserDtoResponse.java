package ru.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoResponse {

    private Long id;

    private String username;

    private String password;

    private LocalDate birthday;

    private List<EmailDto> emailDtoList;

    private List<PhoneDto> phoneDtoList;
}
