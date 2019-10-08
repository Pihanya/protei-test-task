package ru.protei.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ru.protei.model.ServiceUser;

public interface ServiceUserRepository {

  List<Optional<ServiceUser>> findAllById(Iterable<UUID> ids);

  List<ServiceUser> saveAll(Iterable<ServiceUser> entities);

  void flush();

  ServiceUser save(ServiceUser entity);

  ServiceUser saveAndFlush(ServiceUser entity);

  Optional<ServiceUser> findById(UUID id);

  Optional<ServiceUser> findByUsernameOrEmail(String username, String email);

  Optional<ServiceUser> findByUsername(String username);

  Optional<ServiceUser> findByEmail(String email);

  void deleteAll();
}
