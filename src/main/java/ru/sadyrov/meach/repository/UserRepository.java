package ru.sadyrov.meach.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sadyrov.meach.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByLogin(String login);

    User findByEmail(String email);

    Optional<User> findById(Long id);

    List<User> findByActiveIs(boolean isActive);

    User findByActivationCode(String activationCode);

    User findByLoginAndEmail(String login, String email);
}