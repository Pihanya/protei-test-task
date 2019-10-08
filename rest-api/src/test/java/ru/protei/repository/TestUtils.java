package ru.protei.repository;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import ru.protei.api.model.OnlineStatus;
import ru.protei.api.model.User;
import ru.protei.api.model.UserInfo;
import ru.protei.api.utils.TimeBasedUuidGenerator;
import ru.protei.model.ServiceUser;
import ru.protei.model.UserDetails;

class TestUtils {

  public static final Random RANDOM = new Random(1337);

  public static User randomUser() {
    return new User()
        .setId(randomUUID())
        .setEmail(randomEmail())
        .setUsername(randomStr(10));
  }

  public static ServiceUser randomServiceUser() {
    return new ServiceUser()
        .setId(randomUUID())
        .setUsername(randomStr(20))
        .setOnlineStatus(randomModelOnlineStatus())
        .setLastOnlineMillis(System.currentTimeMillis() - 60 * 1000 * (1 + RANDOM.nextInt(10)))
        .setEmail(randomEmail());
  }

  public static UserInfo randomInfo() {
    return new UserInfo()
        .setScreenName(randomStr(20))
        .setPhoneNumber(randomPhoneNumber())
        .setRegisterDate(LocalDateTime.now().minusMinutes(1 + RANDOM.nextInt(10)))
        .setBio(randomStr(300));
  }

  public static UserDetails randomDetails() {
    return new UserDetails()
        .setUserId(randomUUID())
        .setScreenName(randomStr(30))
        .setPhoneNumber(randomPhoneNumber())
        .setBio(randomStr(200));
  }

  public static String randomPhoneNumber() {
    var builder = new StringBuilder("+7");
    RANDOM.ints(10, 0, 10)
          .mapToObj(i -> (char) (i + '0'))
          .forEach(builder::append);
    return builder.toString();
  }

  public static String randomEmail() {
    return randomStr(10) + "@" + randomStr(10) + ".com";
  }

  public static OnlineStatus randomOnlineStatus() {
    return OnlineStatus.values()
        [RANDOM.nextInt(OnlineStatus.values().length)];
  }

  public static ru.protei.model.ServiceUser.OnlineStatus randomModelOnlineStatus() {
    return ru.protei.model.ServiceUser.OnlineStatus.values()
        [RANDOM.nextInt(ru.protei.model.ServiceUser.OnlineStatus.values().length)];
  }

  public static UUID randomUUID() {
    return TimeBasedUuidGenerator.generate();
  }

  public static String randomStr(int size) {
    StringBuilder builder = new StringBuilder(size);
    RANDOM.ints(size, 'a', 'z')
          .mapToObj(i -> (char) i)
          .forEach(builder::append);
    return builder.toString();
  }
}
