package ru.protei.api.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static ru.protei.api.model.ApiResponse.createFailResponseWith;
import static ru.protei.api.model.ApiResponse.createSuccessResponseWith;
import static ru.protei.api.utils.Mappers.mapFrom;
import static ru.protei.api.utils.UserDataValidator.validateEmail;
import static ru.protei.api.utils.UserDataValidator.validatePhoneNumber;
import static ru.protei.api.utils.UserDataValidator.validateUsername;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.protei.api.configuration.UserServiceProperties;
import ru.protei.api.model.ApiResponse;
import ru.protei.api.model.OnlineStatus;
import ru.protei.api.request.CreateUserRequest;
import ru.protei.api.response.ChangeUserOnlineStatusResponse;
import ru.protei.api.response.ChangeUserOnlineStatusResponse.Error;
import ru.protei.api.response.CreateUserError;
import ru.protei.api.response.GetUserInfoResponse;
import ru.protei.api.utils.TimeBasedUuidGenerator;
import ru.protei.model.ServiceUser;
import ru.protei.repository.ServiceUserRepository;
import ru.protei.repository.UserDetailsRepository;

/**
 * Имплементация {@link UserService}
 * {@inheritDoc}
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

  private long onlineStatusTimeout;

  private ServiceUserRepository userRepository;
  private UserDetailsRepository userDetailsRepository;

  public UserServiceImpl(
      UserServiceProperties properties,
      ServiceUserRepository serviceUserCacheRepository,
      UserDetailsRepository userDetailsCacheRepository
  ) {
    checkArgument(
        properties.getOnlineStatusTimeout().toMillis() > 0,
        "Online status timeout should be a positive number"
    );

    this.onlineStatusTimeout = properties.getOnlineStatusTimeout().toMillis();
    this.userRepository = checkNotNull(serviceUserCacheRepository);
    this.userDetailsRepository = checkNotNull(userDetailsCacheRepository);
  }

  public ApiResponse<UUID, CreateUserError> createUser(CreateUserRequest request) {
    var user = request.getUser();
    var userInfo = request.getUserInfo();
    if (user == null || user.getUsername() == null || user.getEmail() == null) {
      return createFailResponseWith(CreateUserError.USER_NOT_PROVIDED);
    }

    if (user.getId() != null) {
      return createFailResponseWith(CreateUserError.USER_ID_SHOULD_NOT_BE_PROVIDED);
    }

    if (userInfo == null || userInfo.getScreenName() == null || userInfo.getPhoneNumber() == null) {
      return createFailResponseWith(CreateUserError.USER_INFO_NOT_PROVIDED);
    }

    var username = user.getUsername().strip();
    var email = user.getEmail().strip().toLowerCase();
    var phoneNumber = userInfo.getPhoneNumber();

    if (!validateUsername(username)) {
      return createFailResponseWith(CreateUserError.INVALID_USERNAME);
    }

    if (!validateEmail(email)) {
      return createFailResponseWith(CreateUserError.INVALID_EMAIL);
    }

    if (!validatePhoneNumber(phoneNumber)) {
      return createFailResponseWith(CreateUserError.INVALID_PHONE_NUMBER);
    }

    var usernameEmailOptional = userRepository.findByUsernameOrEmail(username, email);
    if (usernameEmailOptional.isPresent()) {
      return createFailResponseWith(CreateUserError.EMAIL_OR_USERNAME_OCCUPIED);
    }

    var phoneOptional = userDetailsRepository.findByPhoneNumber(phoneNumber);
    if (phoneOptional.isPresent()) {
      return createFailResponseWith(CreateUserError.PHONE_NUMBER_OCCUPIED);
    }

    var userId = TimeBasedUuidGenerator.generate();
    var databaseUser = userRepository.save(mapFrom(user.setId(userId), OnlineStatus.OFFLINE, -1L));
    userDetailsRepository.save(mapFrom(userId, userInfo).setUser(databaseUser));

    log.info("Created a new user {} with username {}", userId, username);
    return createSuccessResponseWith(userId);
  }

  public ApiResponse<GetUserInfoResponse, GetUserInfoResponse.Error> getUserInfo(UUID userId) {
    if (userId == null) {
      return createFailResponseWith(GetUserInfoResponse.Error.USER_ID_SHOULD_BE_PROVIDED);
    }

    if (userId.version() != 1) {
      return createFailResponseWith(GetUserInfoResponse.Error.INVALID_USER_ID);
    }

    var serviceUserOptional = userRepository.findById(userId);
    if (serviceUserOptional.isEmpty()) {
      return createFailResponseWith(GetUserInfoResponse.Error.USER_NOT_FOUND);
    }
    var serviceUser = serviceUserOptional.get();

    OnlineStatus onlineStatus;
    if (serviceUser.getOnlineStatus() == ServiceUser.OnlineStatus.ONLINE
        && hasExpired(serviceUser.getOnlineStatus(), serviceUser.getLastOnlineMillis())) {
      onlineStatus = OnlineStatus.AWAY;
      log.info("{} was away for more than {}ms. Switching to {}",
          serviceUser.getId(), onlineStatusTimeout, onlineStatus);
      userRepository.save(serviceUser.setOnlineStatus(mapFrom(onlineStatus)));
    } else {
      onlineStatus = mapFrom(serviceUser.getOnlineStatus());
    }

    var detailsOptional = userDetailsRepository.findById(userId);
    if (detailsOptional.isEmpty()) {
      return createFailResponseWith(GetUserInfoResponse.Error.USER_INFO_NOT_FOUND);
    }

    var user = mapFrom(serviceUser);
    var userInfo = mapFrom(detailsOptional.get());
    return createSuccessResponseWith(
        new GetUserInfoResponse()
            .setUser(user)
            .setUserInfo(userInfo)
            .setOnlineStatus(onlineStatus)
    );
  }

  public ApiResponse<ChangeUserOnlineStatusResponse, ChangeUserOnlineStatusResponse.Error> changeUserOnlineStatus(
      UUID userId, OnlineStatus onlineStatus) {
    if (userId == null) {
      return createFailResponseWith(Error.USER_ID_NOT_PROVIDED);
    }

    if (userId.version() != 1) {
      return createFailResponseWith(Error.INVALID_USER_ID);
    }

    if (onlineStatus == null) {
      return createFailResponseWith(ChangeUserOnlineStatusResponse.Error
          .ONLINE_STATUS_NOT_PROVIDED);
    }

    var serviceUserOptional = userRepository.findById(userId);
    if (serviceUserOptional.isEmpty()) {
      return createFailResponseWith(ChangeUserOnlineStatusResponse.Error.USER_NOT_FOUND);
    }

    var serviceUser = serviceUserOptional.get();
    var beforeOnlineStatus = mapFrom(serviceUser.getOnlineStatus());
    if (onlineStatus == OnlineStatus.ONLINE) {
      serviceUser.setOnlineStatus(mapFrom(OnlineStatus.ONLINE))
                 .setLastOnlineMillis(System.currentTimeMillis());
    } else {
      serviceUser.setOnlineStatus(mapFrom(onlineStatus));
    }

    var nowOnlineStatus = mapFrom(userRepository.save(serviceUser).getOnlineStatus());
    return createSuccessResponseWith(
        new ChangeUserOnlineStatusResponse()
            .setUserId(userId)
            .setBeforeStatus(beforeOnlineStatus)
            .setNowStatus(nowOnlineStatus)
    );
  }

  private boolean hasExpired(ServiceUser.OnlineStatus onlineStatus, long lastUpdateMillis) {
    switch (onlineStatus) {
      case ONLINE:
        return (System.currentTimeMillis() - lastUpdateMillis) > onlineStatusTimeout;
      case AWAY:
      case OFFLINE:
        return false;
      default:
        throw new IllegalArgumentException("Unknown online status: " + onlineStatus);
    }
  }
}
