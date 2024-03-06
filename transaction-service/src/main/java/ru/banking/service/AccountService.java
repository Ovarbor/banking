package ru.banking.service;

import org.springframework.stereotype.Service;
import ru.banking.dto.AccountDtoResponse;
import ru.banking.dto.UpdateAccountDtoRequest;

@Service
public interface AccountService {

    AccountDtoResponse sendMoney(Long senderId, Long recipientId, UpdateAccountDtoRequest updateAccountDtoRequest);

    AccountDtoResponse getAccount(Long userId);
}
