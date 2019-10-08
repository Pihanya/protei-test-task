package ru.protei.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import ru.protei.api.model.User;
import ru.protei.api.model.UserInfo;

/**
 * Запрос на создание пользователя
 */
@Data @NonNull
@Accessors(chain = true) @FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRequest {

  /**
   * Основная информация о пользователе
   */
  @JsonProperty(required = true)
  User user;

  /**
   * Детальная информация о пользователе
   */
  @JsonProperty(required = true)
  UserInfo userInfo;
}
