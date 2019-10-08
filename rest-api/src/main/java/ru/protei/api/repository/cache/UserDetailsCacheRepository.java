package ru.protei.api.repository.cache;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.protei.api.configuration.UserServiceProperties;
import ru.protei.model.UserDetails;
import ru.protei.repository.UserDetailsRepository;

@Slf4j
@Component("userDetailsCacheRepository")
public class UserDetailsCacheRepository implements UserDetailsRepository {

  private UserDetailsRepository userDetailsRepository;

  private LoadingCache<UUID, UserDetails> cache;

  public UserDetailsCacheRepository(UserServiceProperties properties,
      UserDetailsRepository userDetailsDatabaseRepository) {
    checkNotNull(properties);
    checkNotNull(properties.getUserDetailsCache());

    var cacheParams = properties.getUserDetailsCache();
    checkNotNull(cacheParams.getExpirationTime());
    checkArgument(
        cacheParams.getSize() > 0,
        "Cache size should be a positive number"
    );

    this.userDetailsRepository = checkNotNull(userDetailsDatabaseRepository);
    this.cache = CacheBuilder.newBuilder()
                             .maximumSize(cacheParams.getSize())
                             .expireAfterWrite(
                                 cacheParams.getExpirationTime().toMillis(),
                                 TimeUnit.MILLISECONDS
                             )
                             .build(new CacheLoader<>() {
                               public UserDetails load(UUID key) {
                                 return userDetailsRepository.findById(key).orElseThrow();
                               }
                             });
  }

  @Override public List<Optional<UserDetails>> findAllById(Iterable<UUID> ids) {
    return StreamSupport.stream(ids.spliterator(), false)
                        .map(id -> {
                          try {
                            return Optional.of(cache.get(id));
                          } catch (ExecutionException | UncheckedExecutionException ex) {
                            return Optional.<UserDetails>empty();
                          } catch (Exception ex) {
                            log.error("Error happened during finding UserDetails by id", ex);
                            throw new RuntimeException(ex);
                          }
                        })
                        .collect(Collectors.toUnmodifiableList());
  }

  @Override public List<UserDetails> saveAll(Iterable<UserDetails> entities) {
    var list = userDetailsRepository.saveAll(entities);
    list.forEach(user -> cache.put(user.getUserId(), user));
    return list;
  }

  @Override public void flush() {
    userDetailsRepository.flush();
  }

  @Override public UserDetails save(UserDetails entity) {
    var user = userDetailsRepository.save(entity);
    cache.put(entity.getUserId(), user);
    return user;
  }

  @Override public UserDetails saveAndFlush(UserDetails entity) {
    var user = userDetailsRepository.saveAndFlush(entity);
    cache.put(entity.getUserId(), user);
    return user;
  }

  @Override public Optional<UserDetails> findById(UUID id) {
    try {
      return Optional.of(cache.get(id));
    } catch (ExecutionException | UncheckedExecutionException ex) {
      return Optional.empty();
    } catch (Exception ex) {
      log.error("Error happened during finding UserDetail by id", ex);
      throw new RuntimeException(ex);
    }
  }

  @Override public Optional<UserDetails> findByPhoneNumber(String phoneNumber) {
    var optional = userDetailsRepository.findByPhoneNumber(phoneNumber);
    optional.ifPresent(details -> cache.put(details.getUserId(), details));
    return optional;
  }

  @Override public void deleteAll() {
    cache.invalidateAll();
    userDetailsRepository.deleteAll();
  }
}
