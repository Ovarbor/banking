package ru.banking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public AccountDtoResponse sendMoney(Long senderId, Long recipientId, UpdateAccountDtoRequest updateAccountDtoRequest) {
        Account senderAccount = accountRepo.findAccountByUserId(senderId).orElseThrow(() ->
                new NotFoundValidationException("Account for sender with id: " + senderId + " not found"));
        Account recipientAccount = accountRepo.findAccountByUserId(recipientId).orElseThrow(() ->
                new NotFoundValidationException("Account for recipient with id: " + recipientId + " not found"));
        if (senderAccount.getBalance().compareTo(updateAccountDtoRequest.getBalance()) >= 0) {
            senderAccount.setBalance(senderAccount.getBalance().subtract(updateAccountDtoRequest.getBalance()));
            recipientAccount.setBalance(recipientAccount.getBalance().add(updateAccountDtoRequest.getBalance()));
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
