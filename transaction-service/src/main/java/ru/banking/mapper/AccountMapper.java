package ru.banking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.banking.dto.AccountDtoResponse;
import ru.banking.model.Account;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "username", source = "user.username")
    AccountDtoResponse toAccountDtoResponse(Account account);
}
