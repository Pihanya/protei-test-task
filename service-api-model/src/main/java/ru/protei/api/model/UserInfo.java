package ru.protei.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/**
 * Детализированная информация о пользователе. Здесь есть все, что не попало в {@link User}
 */
@JsonInclude(Include.NON_NULL)
@Data @NoArgsConstructor
@Accessors(chain = true) @FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfo {

  /**
   * Уникальный идентификатор пользователя
   */
  @JsonProperty(required = true)
  @NonNull String phoneNumber;

  /**
   * Экранное имя пользователя. Например ФИО
   */
  @JsonProperty(required = true)
  @NonNull String screenName;

  /**
   * Описание пользователя
   */
  @JsonProperty
  String bio;

  /**
   * Номер телефона пользователя (уникален)
   */
  @JsonProperty
  LocalDateTime registerDate;
}
