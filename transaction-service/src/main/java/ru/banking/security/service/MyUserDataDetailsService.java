package ru.banking.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.banking.exception.NotFoundValidationException;
import ru.banking.model.User;
import ru.banking.repo.UserRepo;

@Service
@RequiredArgsConstructor
public class MyUserDataDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findUserByUsername(username).orElseThrow(() ->
                new NotFoundValidationException("User with name " + username + " not found"));
        if (username == null) {
            throw new UsernameNotFoundException("User with username: " + user.getUsername() + "not found");
        }
        return new MyUserDataPrincipal(user);
    }
}
