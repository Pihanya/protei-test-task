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
import ru.protei.model.ServiceUser;
import ru.protei.repository.ServiceUserRepository;

@Slf4j
@Component("serviceUserCacheRepository")
public class ServiceUserCacheRepository implements ServiceUserRepository {

  private ServiceUserRepository userRepository;

  private LoadingCache<UUID, ServiceUser> cache;

  public ServiceUserCacheRepository(UserServiceProperties properties,
      ServiceUserRepository serviceUserDatabaseRepository) {
    checkNotNull(properties);
    var cacheProperties = checkNotNull(properties.getServiceUserCache());
    checkNotNull(cacheProperties.getExpirationTime());
    checkArgument(cacheProperties.getSize() > 0, "Cache size should be a positive number");

    this.userRepository = checkNotNull(serviceUserDatabaseRepository);
    this.cache = CacheBuilder.newBuilder()
                             .maximumSize(cacheProperties.getSize())
                             .expireAfterWrite(cacheProperties.getExpirationTime().toMillis(),
                                 TimeUnit.MILLISECONDS)
                             .build(new CacheLoader<>() {
                               public ServiceUser load(UUID key) {
                                 return userRepository.findById(key).orElseThrow();
                               }
                             });
  }

  @Override public List<Optional<ServiceUser>> findAllById(Iterable<UUID> ids) {
    return StreamSupport.stream(ids.spliterator(), false)
                        .map(id -> {
                          try {
                            return Optional.of(cache.get(id));
                          } catch (ExecutionException | UncheckedExecutionException ex) {
                            return Optional.<ServiceUser>empty();
                          } catch (Exception ex) {
                            log.error("Error happened during finding ServiceUsers by ids", ex);
                            throw new RuntimeException(ex);
                          }
                        })
                        .collect(Collectors.toUnmodifiableList());
  }

  @Override public List<ServiceUser> saveAll(Iterable<ServiceUser> entities) {
    var list = userRepository.saveAll(entities);
    list.forEach(entity -> cache.put(entity.getId(), entity));
    return list;
  }

  @Override public void flush() {
    userRepository.flush();
  }

  @Override public ServiceUser save(ServiceUser entity) {
    var user = userRepository.save(entity);
    cache.put(user.getId(), user);
    return user;
  }

  @Override public ServiceUser saveAndFlush(ServiceUser entity) {
    var user = userRepository.saveAndFlush(entity);
    cache.put(user.getId(), user);
    return user;
  }

  @Override public Optional<ServiceUser> findById(UUID id) {
    try {
      return Optional.of(cache.get(id));
    } catch (ExecutionException | UncheckedExecutionException ex) {
      return Optional.empty();
    } catch (Exception ex) {
      log.error("Error happened during finding ServiceUser by id", ex);
      throw ex;
    }
  }

  @Override public Optional<ServiceUser> findByUsernameOrEmail(String username, String email) {
    var optional = userRepository.findByUsernameOrEmail(username, email);
    optional.ifPresent(user -> cache.put(user.getId(), user));
    return optional;
  }

  @Override public Optional<ServiceUser> findByUsername(String username) {
    var optional = userRepository.findByUsername(username);
    optional.ifPresent(user -> cache.put(user.getId(), user));
    return optional;
  }

  @Override public Optional<ServiceUser> findByEmail(String email) {
    var optional = userRepository.findByUsername(email);
    optional.ifPresent(user -> cache.put(user.getId(), user));
    return optional;
  }

  @Override public void deleteAll() {
    cache.invalidateAll();
    userRepository.deleteAll();
  }
}
