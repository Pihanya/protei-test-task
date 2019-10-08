package ru.protei.repository.jpa;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.protei.model.UserDetails;

/**
 * Интерфейс для взаимодействия с данными о пользователе посредством {@code JPA}
 */
public interface UserDetailsJpaRepository extends JpaRepository<UserDetails, UUID> {

  /**
   * Найти пользователя по {@code phoneNumber}
   * @param phoneNumber номер телефона пользователя
   * @return найденный пользователь или его отсутствие
   */
  Optional<UserDetails> findByPhoneNumber(String phoneNumber);
}
