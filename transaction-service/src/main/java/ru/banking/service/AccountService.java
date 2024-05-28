package ru.banking.service;
import ru.banking.dto.AccountDtoResponse;
import ru.banking.dto.UpdateAccountDtoRequest;

public interface AccountService {

    AccountDtoResponse sendMoney(Long senderId, Long recipientId, UpdateAccountDtoRequest updateAccountDtoRequest);

    AccountDtoResponse getAccount(Long userId);
}
