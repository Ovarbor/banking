package ru.banking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.banking.dto.EmailDto;
import ru.banking.model.Email;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface EmailMapper {

    @Mapping(source = "userId", target = "user.id")
    Email toEmail(EmailDto emailDto);

    @Mapping(source = "user.id", target = "userId")
    EmailDto toEmailDto(Email email);
}
