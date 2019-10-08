package ru.protei.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.protei.repository.TestUtils.randomServiceUser;
import static ru.protei.repository.TestUtils.randomUUID;

import java.time.Duration;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import ru.protei.api.configuration.UserServiceProperties;
import ru.protei.api.configuration.UserServiceProperties.CacheProperties;
import ru.protei.api.repository.cache.ServiceUserCacheRepository;
import ru.protei.model.ServiceUser;

public class CacheRepositoriesTests {

  private final int CACHE_SIZE = 10;
  private final Duration EXPIRATION_TIME = Duration.ofSeconds(3);

  private ServiceUserCacheRepository cache;

  private ServiceUserRepository mockUserRepository;

  @BeforeEach public void init() {
    this.mockUserRepository = mock(ServiceUserRepository.class);

    this.cache = new ServiceUserCacheRepository(
        new UserServiceProperties()
            .setServiceUserCache(
                new CacheProperties()
                    .setExpirationTime(EXPIRATION_TIME)
                    .setSize(CACHE_SIZE)
            ),
        mockUserRepository);
  }

  @Test public void saveAndGetFromCacheTest() {
    var id = randomUUID();
    when(mockUserRepository.save(any())).thenReturn(randomServiceUser().setId(id));
    var expectedUser = cache.save(randomServiceUser().setId(id));

    var optional = cache.findById(id);
    assertFalse(optional.isEmpty());

    var actualUser = optional.get();
    assertEquals(expectedUser, actualUser);
  }

  @Test public void overflowCacheTest() {
    when(mockUserRepository.save(any())).thenAnswer(
        (Answer<ServiceUser>) invocation -> invocation.getArgumentAt(0, ServiceUser.class)
    );

    when(mockUserRepository.findById(any())).thenAnswer(
        (Answer<Optional<ServiceUser>>) invocation -> Optional.empty()
    );

    var id = randomUUID();
    var serviceUser = cache.save(randomServiceUser().setId(id));
    Stream.generate(TestUtils::randomServiceUser)
          .limit(CACHE_SIZE)
          .forEach(user -> cache.save(user));

    var optional = cache.findById(id);
    assertTrue(optional.isEmpty());
  }
}