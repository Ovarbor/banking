package ru.banking.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchUserDtoResponse {

    private String username;

    private LocalDate birthday;

    private List<EmailDtoResponse> emailsList;

    private List<PhoneDtoResponse> phonesList;
}
