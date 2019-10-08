package ru.protei.repository.jpa;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.protei.model.ServiceUser;

/**
 * Интерфейс для взаимодействием с сущностью пользователя посредством {@code JPA}
 */
@Repository
public interface ServiceUserJpaRepository extends JpaRepository<ServiceUser, UUID> {
  void deleteAll();

  /**
   * Найти пользователя по совпадению {@code username} или {@code email}
   * @param username имя пользователя
   * @param email электронная почта
   * @return найденный пользователь или его отсутствие
   */
  Optional<ServiceUser> findByUsernameOrEmail(String username, String email);

  /**
   * Найти пользователя по {@code username}
   * @param username имя пользователя
   * @return найденный пользователь или его отсутствие
   */
  Optional<ServiceUser> findByUsername(String username);


  /**
   * Найти пользователя по {@code username}
   * @param email электронная почта пользователя
   * @return найденный пользователь или его отсутствие
   */
  Optional<ServiceUser> findByEmail(String email);
}
