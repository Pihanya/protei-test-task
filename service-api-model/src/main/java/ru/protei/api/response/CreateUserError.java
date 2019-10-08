package ru.protei.api.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public enum CreateUserError {

  /**
   * Данные о пользователе ({@code username}/{@code email}) не были предоставлены, или были предоставлены в неполном
   * объеме
   */
  USER_NOT_PROVIDED(0),

  /**
   * При запросе на создание был передан идентификатор пользователя
   */
  USER_ID_SHOULD_NOT_BE_PROVIDED(1),

  /**
   * Детализированные данные не были переданы или были переданы в неполном обьеме
   */
  USER_INFO_NOT_PROVIDED(2),

  /**
   * {@code username} был передан в неверном формате
   */
  INVALID_USERNAME(3),

  /**
   * {@code email} был передан в неверном формате
   */
  INVALID_EMAIL(4),

  /**
   * {@code phoneNumber} был передан в неверном формате
   */
  INVALID_PHONE_NUMBER(5),

  /**
   * Уже существует пользователь с переданным {@code email}/{@code username}
   */
  EMAIL_OR_USERNAME_OCCUPIED(6),

  /**
   * Уже существует пользователь с переданным {@code phoneNumber}
   */
  PHONE_NUMBER_OCCUPIED(7);

  @JsonValue
  private final int code;

  private static Map<Integer, CreateUserError> codeToStatus =
      Stream.of(CreateUserError.values())
            .collect(Collectors.toMap(CreateUserError::code, status -> status));

  @JsonCreator
  public static CreateUserError fromCode(int code) {
    CreateUserError status = codeToStatus.get(code);
    if (status == null) {
      throw new IllegalArgumentException("Could not find CreateUserError with code: " + code);
    }
    return status;
  }
}
