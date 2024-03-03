package ru.banking.mapper;

import org.mapstruct.Mapper;
import ru.banking.dto.AccountDtoResponse;
import ru.banking.model.Account;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountDtoResponse toAccountDtoResponse(Account account);
}
