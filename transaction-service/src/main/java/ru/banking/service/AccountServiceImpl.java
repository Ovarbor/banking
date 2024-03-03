package ru.banking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.banking.dto.AccountDtoResponse;
import ru.banking.dto.UpdateAccountDtoRequest;
import ru.banking.exception.ConflictException;
import ru.banking.exception.NotFoundValidationException;
import ru.banking.mapper.AccountMapper;
import ru.banking.model.Account;
import ru.banking.repo.AccountRepo;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepo accountRepo;
    private final AccountMapper accountMapper;

    @Override
    public AccountDtoResponse sendMoney(Long senderId, Long recipientId, UpdateAccountDtoRequest updateAccountDtoRequest) {
        Account senderAccount = accountRepo.findAccountByUserId(senderId).orElseThrow(() ->
                new NotFoundValidationException("Account for sender with id: " + senderId + " not found"));
        Account recipientAccount = accountRepo.findAccountByUserId(recipientId).orElseThrow(() ->
                new NotFoundValidationException("Account for recipient with id: " + recipientId + " not found"));
        if ((senderAccount.getBalance() - updateAccountDtoRequest.getBalance()) >= 0.0) {
            senderAccount.setBalance(senderAccount.getBalance() - updateAccountDtoRequest.getBalance());
            recipientAccount.setBalance(recipientAccount.getBalance() + updateAccountDtoRequest.getBalance());
            accountRepo.save(senderAccount);
            accountRepo.save(recipientAccount);
            return accountMapper.toAccountDtoResponse(senderAccount);
        } else {
            throw new ConflictException("insufficient funds in account");
        }
    }

    @Override
    public AccountDtoResponse getAccount(Long userId) {
        Account account = accountRepo.findAccountByUserId(userId).orElseThrow(() ->
                new NotFoundValidationException("Account for User with id: " + userId + " not found"));
        return accountMapper.toAccountDtoResponse(account);
    }
}
