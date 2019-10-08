package ru.protei.repository;

import static java.lang.System.currentTimeMillis;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.protei.api.utils.Mappers.mapFrom;
import static ru.protei.repository.TestUtils.randomDetails;
import static ru.protei.repository.TestUtils.randomInfo;
import static ru.protei.repository.TestUtils.randomModelOnlineStatus;
import static ru.protei.repository.TestUtils.randomOnlineStatus;
import static ru.protei.repository.TestUtils.randomServiceUser;
import static ru.protei.repository.TestUtils.randomUUID;
import static ru.protei.repository.TestUtils.randomUser;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.stubbing.Answer;
import ru.protei.api.configuration.UserServiceProperties;
import ru.protei.api.configuration.UserServiceProperties.CacheProperties;
import ru.protei.api.model.OnlineStatus;
import ru.protei.api.request.CreateUserRequest;
import ru.protei.api.response.CreateUserError;
import ru.protei.api.response.GetUserInfoResponse;
import ru.protei.api.service.UserService;
import ru.protei.api.service.UserServiceImpl;
import ru.protei.model.ServiceUser;
import ru.protei.model.UserDetails;

public class UserServiceImplUnitTests {

  private static final int ONLINE_STATUS_TIMEOUT = 1_000;

  private static final int CACHE_SIZE = 5;
  private static final int CACHE_EXPIRATION_TIME = 1_000;

  private UserService service;

  private ServiceUserRepository mockUserRepository;
  private UserDetailsRepository mockUserDetailsRepository;

  @BeforeEach void init() {
    this.mockUserRepository = mock(
        ServiceUserRepository.class,
        (Answer) invocation -> {
          if (Arrays.asList("findAllById", "saveAll", "flush", "saveAndFlush", "deleteAll")
                    .contains(invocation.getMethod().getName())
          ) {
            throw new IllegalStateException();
          } else {
            return null;
          }
        }
    );
    this.mockUserDetailsRepository = mock(
        UserDetailsRepository.class,
        (Answer) invocation -> {
          if (Arrays.asList("findAllById", "saveAll", "flush", "saveAndFlush", "deleteAll")
                    .contains(invocation.getMethod().getName())
          ) {
            throw new IllegalStateException();
          } else {
            return null;
          }
        }
    );

    when(mockUserRepository.save(any())).thenAnswer(
        (Answer<ServiceUser>) invocation -> invocation.getArgumentAt(0, ServiceUser.class)
    );

    when(mockUserDetailsRepository.save(any())).thenAnswer(
        (Answer<UserDetails>) invocation -> invocation.getArgumentAt(0, UserDetails.class)
    );

    when(mockUserRepository.findById(any())).thenAnswer(
        (Answer<Optional<ServiceUser>>) invocation ->
            Optional.of(randomServiceUser().setId(invocation.getArgumentAt(0, UUID.class)))
    );

    when(mockUserRepository.findByUsernameOrEmail(any(), any())).thenAnswer(
        (Answer<Optional<ServiceUser>>) invocation ->
            Optional.of(randomServiceUser()
                .setUsername(invocation.getArgumentAt(0, String.class))
                .setEmail(invocation.getArgumentAt(1, String.class)))
    );

    when(mockUserRepository.findByEmail(any())).thenAnswer(
        (Answer<Optional<ServiceUser>>) invocation ->
            Optional.of(randomServiceUser()
                .setEmail(invocation.getArgumentAt(0, String.class)))
    );

    when(mockUserDetailsRepository.findByPhoneNumber(any())).thenAnswer(
        (Answer<Optional<UserDetails>>) invocation ->
            Optional.of(randomDetails().setPhoneNumber(invocation.getArgumentAt(0, String.class)))
    );

    when(mockUserDetailsRepository.findById(any())).thenAnswer(
        (Answer<Optional<UserDetails>>) invocation ->
            Optional.of(randomDetails().setUserId(invocation.getArgumentAt(0, UUID.class)))
    );

    this.service = new UserServiceImpl(
        new UserServiceProperties()
            .setOnlineStatusTimeout(Duration.ofMillis(ONLINE_STATUS_TIMEOUT))
            .setServiceUserCache(
                new CacheProperties()
                    .setSize(CACHE_SIZE)
                    .setExpirationTime(Duration.ofMillis(CACHE_EXPIRATION_TIME))
            )
            .setUserDetailsCache(
                new CacheProperties()
                    .setSize(CACHE_SIZE)
                    .setExpirationTime(Duration.ofMillis(CACHE_EXPIRATION_TIME))
            ), mockUserRepository, mockUserDetailsRepository
    );
  }

  @Test void positiveCreateUserTest() {
    var user = randomUser().setId(null);
    var info = randomInfo();
    AtomicReference<UUID> id = new AtomicReference<>(randomUUID());

    when(mockUserRepository.save(any()))
        .thenAnswer((Answer<ServiceUser>) invocation -> {
          var serviceUser = invocation.getArgumentAt(0, ServiceUser.class);
          id.set(serviceUser.getId());
          return serviceUser;
        });

    when(mockUserDetailsRepository.save(any()))
        .thenAnswer((Answer<UserDetails>) invocation -> {
              var details = invocation.getArgumentAt(0, UserDetails.class);
              assertEquals(id.get(), details.getUser().getId());
              return details;
            }
        );

    when(mockUserDetailsRepository.findByPhoneNumber(any())).thenReturn(Optional.empty());
    when(mockUserRepository.findByEmail(any())).thenReturn(Optional.empty());
    when(mockUserRepository.findByUsernameOrEmail(any(), any())).thenReturn(Optional.empty());

    var request = new CreateUserRequest().setUser(user).setUserInfo(info);
    var response = service.createUser(request);

    assertTrue(response.isSuccess());
    assertEquals(id.get(), response.getResult());
  }

  @Test void negativeCreateUserWithUuidTest() {
    var id = UUID.randomUUID();
    var user = randomUser().setId(id);
    var info = randomInfo();

    var request = new CreateUserRequest().setUser(user).setUserInfo(info);
    var response = service.createUser(request);

    assertFalse(response.isSuccess());
    assertEquals(CreateUserError.USER_ID_SHOULD_NOT_BE_PROVIDED, response.getErrorCode());
  }

  @Test void negativeCreateUserWithNoUserProvidedTest() {
    var request = new CreateUserRequest().setUser(null).setUserInfo(randomInfo());
    var response = service.createUser(request);

    assertFalse(response.isSuccess());
    assertEquals(CreateUserError.USER_NOT_PROVIDED, response.getErrorCode());
  }

  @Test void negativeCreateUserWithNoUserInfoProvidedTest() {
    var request = new CreateUserRequest().setUser(randomUser().setId(null)).setUserInfo(null);
    var response = service.createUser(request);

    assertFalse(response.isSuccess());
    assertEquals(CreateUserError.USER_INFO_NOT_PROVIDED, response.getErrorCode());
  }

  @MethodSource("positiveGetUserInfoTestCases")
  @ParameterizedTest void positiveGetUserInfoTest(
      ServiceUser.OnlineStatus prev, long lastOnline,
      OnlineStatus cur
  ) {
    var id = randomUUID();
    when(mockUserRepository.findById(id))
        .thenReturn(Optional.of(randomServiceUser()
            .setId(id)
            .setOnlineStatus(prev)
            .setLastOnlineMillis(lastOnline)));

    when(mockUserDetailsRepository.findById(id))
        .thenReturn(Optional.of(randomDetails().setUserId(id)));

    var response = service.getUserInfo(id);
    assertTrue(response.isSuccess());

    var result = response.getResult();

    var user = result.getUser();
    assertNotNull(user);
    assertEquals(id, user.getId());

    var userInfo = result.getUserInfo();
    assertNotNull(userInfo);

    assertEquals(cur, result.getOnlineStatus());
  }

  private static Stream<Arguments> positiveGetUserInfoTestCases() {
    return Stream.of(
        Arguments.of(ServiceUser.OnlineStatus.OFFLINE, currentTimeMillis(), OnlineStatus.OFFLINE),
        Arguments.of(ServiceUser.OnlineStatus.OFFLINE, 0L, OnlineStatus.OFFLINE),
        Arguments.of(ServiceUser.OnlineStatus.OFFLINE, -1L, OnlineStatus.OFFLINE),
        Arguments.of(ServiceUser.OnlineStatus.AWAY, currentTimeMillis(), OnlineStatus.AWAY),
        Arguments.of(ServiceUser.OnlineStatus.AWAY, 0L, OnlineStatus.AWAY),
        Arguments.of(ServiceUser.OnlineStatus.AWAY, -1L, OnlineStatus.AWAY),
        Arguments.of(ServiceUser.OnlineStatus.ONLINE, currentTimeMillis(), OnlineStatus.ONLINE),
        Arguments.of(ServiceUser.OnlineStatus.ONLINE, 0L, OnlineStatus.AWAY),
        Arguments.of(ServiceUser.OnlineStatus.ONLINE, -1L, OnlineStatus.AWAY)
    );
  }

  @Test void negativeGetUserInfoUserIdNotProvidedTest() {
    var response = service.getUserInfo(null);
    assertTrue(response.isError());
    assertEquals(GetUserInfoResponse.Error.USER_ID_SHOULD_BE_PROVIDED, response.getErrorCode());
  }

  @Test void negativeGetUserInfoUserNotFoundTest() {
    when(mockUserRepository.findById(any())).thenReturn(Optional.empty());

    var response = service.getUserInfo(randomUUID());
    assertTrue(response.isError());
    assertEquals(GetUserInfoResponse.Error.USER_NOT_FOUND, response.getErrorCode());
  }

  @Test void negativeGetUserInfoUserInfoNotFoundTest() {
    when(mockUserDetailsRepository.findById(any())).thenReturn(Optional.empty());

    var response = service.getUserInfo(randomUUID());
    assertTrue(response.isError());
    assertEquals(GetUserInfoResponse.Error.USER_INFO_NOT_FOUND, response.getErrorCode());
  }

  @RepeatedTest(value = 50) void positiveChangeUserOnlineStatusTest() {
    var id = randomUUID();
    var beforeOnlineStatus = randomModelOnlineStatus();
    var nowOnlineStatus = randomOnlineStatus();
    Double lastOnline = currentTimeMillis() * Math.random() / 2;
    when(mockUserRepository.findById(id)).thenAnswer(
        (Answer<Optional<ServiceUser>>) invocation -> Optional.of(
            new ServiceUser()
                .setId(id)
                .setOnlineStatus(beforeOnlineStatus)
                .setLastOnlineMillis(lastOnline.longValue())
        )
    );

    when(mockUserRepository.save(any())).thenAnswer(
        (Answer<ServiceUser>) invocation -> {
          var savedUser = invocation.getArgumentAt(0, ServiceUser.class);
          return new ServiceUser()
              .setId(savedUser.getId())
              .setOnlineStatus(savedUser.getOnlineStatus())
              .setLastOnlineMillis(savedUser.getLastOnlineMillis());
        }
    );

    var response = service.changeUserOnlineStatus(id, nowOnlineStatus);
    assertTrue(response.isSuccess());

    var result = response.getResult();

    assertNotNull(result.getBeforeStatus());
    assertNotNull(result.getNowStatus());
    assertNotNull(result.getUserId());

    assertEquals(mapFrom(beforeOnlineStatus), result.getBeforeStatus());
    assertEquals(nowOnlineStatus, result.getNowStatus());
    assertEquals(id, result.getUserId());
  }
}
