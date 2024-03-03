package ru.banking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.banking.dto.CreateUserDtoRequest;
import ru.banking.dto.UpdateAccountDtoRequest;
import ru.banking.dto.UserDtoResponse;
import ru.banking.exception.ConflictException;
import ru.banking.exception.NotFoundValidationException;
import java.time.LocalDate;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor
@Transactional
@AutoConfigureMockMvc
public class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    private CreateUserDtoRequest createUserOneDtoRequest;
    private CreateUserDtoRequest createUserTwoDtoRequest;
    private UpdateAccountDtoRequest updateAccountDtoRequest1;
    private UpdateAccountDtoRequest updateAccountDtoRequest2;


    @BeforeEach
    void beforeEach() {
        createUserOneDtoRequest = new CreateUserDtoRequest("userOne", "userOne",
                "89087654324", "userOne@mail.ru", 100.0, LocalDate.of(2012, 4, 5));
        createUserTwoDtoRequest = new CreateUserDtoRequest("userTwo", "userTwo",
                "89147884725", "userTwo@mail.ru", 110.15, LocalDate.of(2007, 8, 12));
        updateAccountDtoRequest1 = new UpdateAccountDtoRequest(17.30);
        updateAccountDtoRequest2 = new UpdateAccountDtoRequest(28.0);
    }

    @Test
    void shouldSendMoneyFromAccountOneToAccountTwoWhenOk() {
        updateAccountDtoRequest1 = new UpdateAccountDtoRequest(17.30);
        updateAccountDtoRequest2 = new UpdateAccountDtoRequest(28.0);
        UserDtoResponse userOneDtoResponse = userService.addUser(createUserOneDtoRequest);
        UserDtoResponse userTwoDtoResponse = userService.addUser(createUserTwoDtoRequest);
        double userOneUpdateBalance = createUserOneDtoRequest.getBalance() - updateAccountDtoRequest1.getBalance();
        double userTwoUpdateBalance = createUserTwoDtoRequest.getBalance() + updateAccountDtoRequest1.getBalance();
        assertThat(accountService.getAccount(userOneDtoResponse.getId()).getBalance(),
                equalTo(createUserOneDtoRequest.getBalance()));
        accountService.sendMoney(userOneDtoResponse.getId(), userTwoDtoResponse.getId(), updateAccountDtoRequest1);
        assertThat(accountService.getAccount(userOneDtoResponse.getId()).getBalance(),
                equalTo(userOneUpdateBalance));
        assertThat(accountService.getAccount(userTwoDtoResponse.getId()).getBalance(),
                equalTo(userTwoUpdateBalance));
        userOneUpdateBalance = accountService.getAccount(userOneDtoResponse.getId()).getBalance()
                + updateAccountDtoRequest2.getBalance();
        userTwoUpdateBalance = accountService.getAccount(userTwoDtoResponse.getId()).getBalance()
                - updateAccountDtoRequest2.getBalance();
        accountService.sendMoney(userTwoDtoResponse.getId(), userOneDtoResponse.getId(), updateAccountDtoRequest2);
        assertThat(accountService.getAccount(userOneDtoResponse.getId()).getBalance(),
                equalTo(userOneUpdateBalance));
        assertThat(accountService.getAccount(userTwoDtoResponse.getId()).getBalance(),
                equalTo(userTwoUpdateBalance));
    }


    @Test
    void shouldThrowExceptionWhenSendMoreThatHaveOnAccount() {
        updateAccountDtoRequest1 = new UpdateAccountDtoRequest(101.0);
        updateAccountDtoRequest2 = new UpdateAccountDtoRequest(200.0);
        UserDtoResponse userOneDtoResponse = userService.addUser(createUserOneDtoRequest);
        UserDtoResponse userTwoDtoResponse = userService.addUser(createUserTwoDtoRequest);
        assertThrows(ConflictException.class, () -> accountService.sendMoney(userOneDtoResponse.getId(),
                userTwoDtoResponse.getId(), updateAccountDtoRequest1));
        assertThrows(ConflictException.class, () -> accountService.sendMoney(userTwoDtoResponse.getId(),
                userOneDtoResponse.getId(), updateAccountDtoRequest2));
    }

    @Test
    void shouldThrowExceptionWhenSendToRecipientNotFound() {
        updateAccountDtoRequest1 = new UpdateAccountDtoRequest(15.0);
        updateAccountDtoRequest2 = new UpdateAccountDtoRequest(13.0);
        UserDtoResponse userOneDtoResponse = userService.addUser(createUserOneDtoRequest);
        UserDtoResponse userTwoDtoResponse = userService.addUser(createUserTwoDtoRequest);
        assertThrows(NotFoundValidationException.class, () -> accountService.sendMoney(userOneDtoResponse.getId(),
                7L, updateAccountDtoRequest1));
        assertThrows(NotFoundValidationException.class, () -> accountService.sendMoney(userTwoDtoResponse.getId(),
                8L, updateAccountDtoRequest2));
    }
}
