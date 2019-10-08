package ru.protei.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

/**
 * {@link Enum} отвечающий за состояние пользователя
 */
@Getter
public enum OnlineStatus {
  /**
   * Пользователь онлайн
   */
  ONLINE,

  /**
   * Пользователь временно отошел
   */
  AWAY,

  /**
   * Пользователь не в сети
   */
  OFFLINE;

  @JsonValue
  private String code = this.name();

  private static Map<Integer, OnlineStatus> codeToStatus =
      Stream.of(OnlineStatus.values())
            .collect(Collectors
                .toMap(status -> status.getCode().toLowerCase().hashCode(), status -> status));

  @JsonCreator
  public static OnlineStatus fromCode(String code) {
    if (code == null) {
      return null;
    } else {
      return codeToStatus.get(code.hashCode());
    }
  }
}
