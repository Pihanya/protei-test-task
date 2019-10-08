package ru.protei.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ru.protei.model.UserDetails;

public interface UserDetailsRepository {

  List<Optional<UserDetails>> findAllById(Iterable<UUID> ids);

  List<UserDetails> saveAll(Iterable<UserDetails> entities);

  void flush();

  UserDetails save(UserDetails entity);

  UserDetails saveAndFlush(UserDetails entity);

  Optional<UserDetails> findById(UUID id);

  Optional<UserDetails> findByPhoneNumber(String phoneNumber);

  void deleteAll();
}
