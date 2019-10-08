package ru.protei.api.repository;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import ru.protei.model.ServiceUser;
import ru.protei.repository.ServiceUserRepository;
import ru.protei.repository.jpa.ServiceUserJpaRepository;

@Repository("serviceUserDatabaseRepository")
public class ServiceUserDatabaseRepository implements ServiceUserRepository {

  private ServiceUserJpaRepository jpaRepository;

  public ServiceUserDatabaseRepository(ServiceUserJpaRepository serviceUserJpaRepository) {
    this.jpaRepository = checkNotNull(serviceUserJpaRepository);
  }

  @Override public List<Optional<ServiceUser>> findAllById(Iterable<UUID> ids) {
    return jpaRepository.findAllById(ids).stream()
                        .map(Optional::ofNullable)
                        .collect(Collectors.toList());
  }

  @Override public List<ServiceUser> saveAll(Iterable<ServiceUser> entities) {
    return jpaRepository.saveAll(entities);
  }

  @Override public void flush() {
    jpaRepository.flush();
  }

  @Override public ServiceUser save(ServiceUser entity) {
    return jpaRepository.save(entity);
  }

  @Override public ServiceUser saveAndFlush(ServiceUser entity) {
    return jpaRepository.saveAndFlush(entity);
  }

  @Override public Optional<ServiceUser> findById(UUID id) {
    return jpaRepository.findById(id);
  }

  @Override public Optional<ServiceUser> findByUsernameOrEmail(String username, String email) {
    return jpaRepository.findByUsernameOrEmail(username, email);
  }

  @Override public Optional<ServiceUser> findByUsername(String username) {
    return jpaRepository.findByUsername(username);
  }

  @Override public Optional<ServiceUser> findByEmail(String email) {
    return jpaRepository.findByEmail(email);
  }

  @Override public void deleteAll() {
    jpaRepository.deleteAll();
  }
}
