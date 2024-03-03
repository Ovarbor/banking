package ru.banking.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.banking.dto.UserDtoResponse;
import ru.banking.model.User;
import java.util.List;

@Mapper(componentModel = "spring",
        uses = {EmailMapper.class, PhoneMapper.class, AccountMapper.class})
public interface UserMapper {

    @Mapping(source = "emailDtoList", target = "emailList")
    @Mapping(source = "phoneDtoList", target = "phoneList")
    User toUser(UserDtoResponse userDtoResponse);

    @Mapping(source = "emailList", target = "emailDtoList")
    @Mapping(source = "phoneList", target = "phoneDtoList")
    UserDtoResponse toUserDtoResponse(User user);

    @Mapping(source = "emailList", target = "emailDtoList")
    @Mapping(source = "phoneList", target = "phoneDtoList")
    List<UserDtoResponse> toUserDtoResponseList(List<User> userList);
}
