package ru.sadyrov.meach.domain;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity(name = "usr")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private Long id;

    @NotBlank(message = "Введите ваш e-mail")
    @Email(message = "Введите корректный e-mail")
    private String email;

    @NotBlank(message = "Введите логин")
//    @Pattern(regexp = "^[a-zA-Z]+$", message = "Логин не должен содержать кириллицу")
    private String login;

    //    @NotBlank(message = "Введите ваш пароль")
//    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
//            message = "Пароль должен содержать минимум 8 символов, 1 букву, 1 цифру и 1 спецсимвол")
    private String password;

    @NotBlank(message = "Введите ваше имя")
    @Length(min = 2, max = 20, message = "Размер имени должен иметь от 2 до 20 символов")
//    @Pattern(regexp = "^[a-zA-Zа-яёА-ЯЁ]+$", message = "Введите ваше имя корректно")
    private String firstName;

    @NotBlank(message = "Введите вашу фамилию")
    @Length(min = 2, max = 20, message = "Размер фамилии должен иметь от 2 до 20 символов")
//    @Pattern(regexp = "^[a-zA-Zа-яёА-ЯЁ]+$", message = "Введите вашу фамилию корректно")
    private String secondName;

    @NotBlank(message = "Введит ваш город")
//    @Pattern(regexp = "^[a-zA-Zа-яёА-ЯЁ]+$", message = "Введите ваш город корректно")
    private String city;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    private String activationCode;

    @NotNull
    private boolean active;
//
//    @OneToMany
//    private Set<Article> articles;

    @OneToMany
    private Set<User> sentRequests;

    @OneToMany
    private Set<User> receivedRequests;

    @ManyToMany
    private Set<User> friends;

    @ManyToMany
    private Set<Cathegory> cathegories;
}
