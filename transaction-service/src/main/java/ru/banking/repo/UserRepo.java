package ru.banking.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.banking.model.Email;
import ru.banking.model.Phone;
import ru.banking.model.User;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {

    Optional<User> findUserByUsername(String username);

    @Query(value = "SELECT u FROM User u JOIN u.emailsList e " +
            "WHERE u.username = :param OR e.email = :param")
    User findUserByParam(@Param("param") String param);

    @Query(value = "SELECT u.username FROM User u")
    List<String> findAllNames();

    @Query(value = "SELECT e.emailsList FROM User e")
    List<Email> findAllEmails();

    @Query(value = "SELECT p.phonesList FROM User p")
    List<Phone> findAllPhones();
}
