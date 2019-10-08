package ru.protei.api.repository;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import ru.protei.model.UserDetails;
import ru.protei.repository.UserDetailsRepository;
import ru.protei.repository.jpa.UserDetailsJpaRepository;

@Repository("userDetailsDatabaseRepository")
public class UserDetailsDatabaseRepository implements UserDetailsRepository {

  private UserDetailsJpaRepository jpaRepository;

  public UserDetailsDatabaseRepository(UserDetailsJpaRepository userDetailsJpaRepository) {
    this.jpaRepository = checkNotNull(userDetailsJpaRepository);
  }

  @Override public List<Optional<UserDetails>> findAllById(Iterable<UUID> ids) {
    return jpaRepository.findAllById(ids).stream()
                        .map(Optional::ofNullable)
                        .collect(Collectors.toUnmodifiableList());
  }

  @Override public List<UserDetails> saveAll(Iterable<UserDetails> entities) {
    return jpaRepository.saveAll(entities);
  }

  @Override public void flush() {
    jpaRepository.flush();
  }

  @Override public UserDetails save(UserDetails entity) {
    return jpaRepository.save(entity);
  }

  @Override public UserDetails saveAndFlush(UserDetails entity) {
    return jpaRepository.saveAndFlush(entity);
  }

  @Override public Optional<UserDetails> findById(UUID id) {
    return jpaRepository.findById(id);
  }

  @Override public Optional<UserDetails> findByPhoneNumber(String phoneNumber) {
    return jpaRepository.findByPhoneNumber(phoneNumber);
  }

  @Override public void deleteAll() {
    jpaRepository.deleteAll();
  }
}
