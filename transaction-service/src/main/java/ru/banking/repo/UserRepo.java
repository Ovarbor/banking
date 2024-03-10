package ru.banking.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.banking.model.User;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {

    Optional<User> findUserByUsername(String username);

    @Query(value = "SELECT u.username FROM User u")
    List<String> findAllNames();

    @Query(value = "SELECT e.emailsList FROM User e")
    List<String> findAllEmails();

    @Query(value = "SELECT p.phonesList FROM User p")
    List<String> findAllPhones();
}
