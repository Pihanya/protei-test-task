package ru.protei.api.configuration;

import java.time.Duration;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("service")
@Data
@Accessors(chain = true) @FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceProperties {

  Duration onlineStatusTimeout = Duration.ofMinutes(1);

  CacheProperties serviceUserCache = new CacheProperties()
      .setExpirationTime(Duration.ofMinutes(10))
      .setSize(100_000);

  CacheProperties userDetailsCache = new CacheProperties()
      .setExpirationTime(Duration.ofMinutes(5))
      .setSize(100_000);

  @Data
  @Accessors(chain = true) @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class CacheProperties {
    Duration expirationTime;
    int size;
  }
}
