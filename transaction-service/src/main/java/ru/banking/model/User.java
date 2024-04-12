package ru.banking.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "birthday")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @ElementCollection
    @CollectionTable(name = "phones", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "phone")
    @AttributeOverrides({
            @AttributeOverride(name = "phone", column = @Column(name = "phone"))
    })
    private List<Phone> phonesList;

    @ElementCollection
    @CollectionTable(name = "emails", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "email")
    @AttributeOverrides({
            @AttributeOverride(name = "email", column = @Column(name = "email"))
    })
    private List<Email> emailsList;
}
