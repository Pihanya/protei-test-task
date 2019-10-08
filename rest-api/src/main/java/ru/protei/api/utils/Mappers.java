package ru.protei.api.utils;

import java.util.UUID;
import ru.protei.api.model.OnlineStatus;
import ru.protei.api.model.User;
import ru.protei.api.model.UserInfo;
import ru.protei.model.ServiceUser;
import ru.protei.model.UserDetails;

public class Mappers {

  public static ServiceUser mapFrom(User user, OnlineStatus onlineStatus, Long lastOnlineMillis) {
    return new ServiceUser()
        .setId(user.getId())
        .setUsername(user.getUsername())
        .setEmail(user.getEmail())
        .setOnlineStatus(mapFrom(onlineStatus))
        .setLastOnlineMillis(lastOnlineMillis == null ? -1 : lastOnlineMillis);
  }

  public static User mapFrom(ServiceUser user) {
    return new User()
        .setId(user.getId())
        .setUsername(user.getUsername())
        .setEmail(user.getEmail());
  }

  public static ServiceUser.OnlineStatus mapFrom(OnlineStatus onlineStatus) {
    switch (onlineStatus) {
      case ONLINE:
        return ServiceUser.OnlineStatus.ONLINE;
      case AWAY:
        return ServiceUser.OnlineStatus.AWAY;
      case OFFLINE:
        return ServiceUser.OnlineStatus.OFFLINE;
      default:
        throw new IllegalArgumentException("Unknown online status: " + onlineStatus);
    }
  }

  public static OnlineStatus mapFrom(ServiceUser.OnlineStatus onlineStatus) {
    switch (onlineStatus) {
      case ONLINE:
        return OnlineStatus.ONLINE;
      case AWAY:
        return OnlineStatus.AWAY;
      case OFFLINE:
        return OnlineStatus.OFFLINE;
      default:
        throw new IllegalStateException("Unknown online status: " + onlineStatus);
    }
  }

  public static UserDetails mapFrom(UUID userId, UserInfo userInfo) {
    return new UserDetails()
        .setUserId(userId)
        .setScreenName(userInfo.getScreenName())
        .setBio(userInfo.getBio())
        .setPhoneNumber(userInfo.getPhoneNumber());
  }

  public static UserInfo mapFrom(UserDetails userDetails) {
    return new UserInfo()
        .setScreenName(userDetails.getScreenName())
        .setBio(userDetails.getBio())
        .setPhoneNumber(userDetails.getPhoneNumber())
        .setRegisterDate(userDetails.getCreateDate());
  }
}
