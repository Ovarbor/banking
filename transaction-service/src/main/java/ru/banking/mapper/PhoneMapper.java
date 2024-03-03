package ru.banking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.banking.dto.PhoneDto;
import ru.banking.model.Phone;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface PhoneMapper {

    @Mapping(source = "userId", target = "user.id")
    Phone toPhone(PhoneDto phoneDto);

    @Mapping(source = "user.id", target = "userId")
    PhoneDto toPhoneDto(Phone phone);
}
