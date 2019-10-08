package ru.protei.api.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;
import java.util.UUID;
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

/**
 * Ответ на запрос на изменение статуса пользователя
 */
@Data @NonNull
@Accessors(chain = true) @FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeUserOnlineStatusResponse {

  /**
   * Идентификатор пользователя
   */
  @JsonProperty(required = true)
  UUID userId;

  /**
   * Статус, в котором пользователь пребывал до запроса
   */
  @JsonProperty(required = true)
  OnlineStatus beforeStatus;

  /**
   * Статус, в котором пользователь пребывает в данный момент
   */
  @JsonProperty(required = true)
  OnlineStatus nowStatus;

  @Getter
  @AllArgsConstructor
  @Accessors(fluent = true)
  public enum Error {
    /**
     * Идентификатор пользователя не был предоставлен
     */
    USER_ID_NOT_PROVIDED(0),

    /**
     * Предоставленный идентификатор пользователя не соответствует стандарту UUIDv1
     */
    INVALID_USER_ID(1),

    /**
     * Статус пользователя не был предоставлен
     */
    ONLINE_STATUS_NOT_PROVIDED(2),

    /**
     * Пользователь с переданным идентификатором не был найден
     */
    USER_NOT_FOUND(3);


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
