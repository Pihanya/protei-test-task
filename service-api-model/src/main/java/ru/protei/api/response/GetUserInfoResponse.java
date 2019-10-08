package ru.protei.api.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import ru.protei.api.model.OnlineStatus;
import ru.protei.api.model.User;
import ru.protei.api.model.UserInfo;

/**
 * Ответ на запрос о получении информации пользователя
 */
@Data @NonNull
@Accessors(chain = true) @FieldDefaults(level = AccessLevel.PRIVATE)
public class GetUserInfoResponse {

  /**
   * Основная информация о пользователе
   */
  @JsonProperty(required = true)
  User user;

  /**
   * Детальная информция о пользователе
   */
  @JsonProperty(required = true)
  UserInfo userInfo;

  /**
   * Текущий статус пользователя
   */
  @JsonProperty(required = true)
  OnlineStatus onlineStatus;

  @Getter
  @AllArgsConstructor
  @Accessors(fluent = true)
  public enum Error {
    /**
     * Идентификатор пользователя не был предоставлен
     */
    USER_ID_SHOULD_BE_PROVIDED(0),

    /**
     * Предоставленный идентификатор пользователя не соответствует стандарту UUIDv1
     */
    INVALID_USER_ID(1),

    /**
     * Пользователь с переданным идентификатором не был найден
     */
    USER_NOT_FOUND(2),

    /**
     * Не было найдено детализированной информации о пользователе (критическая ошибка)
     */
    USER_INFO_NOT_FOUND(3);

    @JsonValue
    private int code;

    private static Map<Integer, Error> codeToStatus =
        Stream.of(Error.values())
              .collect(Collectors.toMap(Error::code, status -> status));

    @JsonCreator
    public static Error fromCode(int code) {
      Error status = codeToStatus.get(code);
      if (status == null) {
        throw new IllegalArgumentException("Could not find Error with code: " + code);
      }
      return status;
    }
  }
}
