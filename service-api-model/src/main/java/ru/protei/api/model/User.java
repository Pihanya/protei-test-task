package ru.protei.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/**
 * Основная информация о пользователе: его идентификатор, имя и почта
 */
@JsonInclude(Include.NON_NULL)
@Data @NoArgsConstructor
@Accessors(chain = true) @FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

  /**
   * Уникальный идентификатор пользователя
   */
  @JsonProperty
  UUID id;

  /**
   * Имя пользователя
   */
  @JsonProperty(required = true)
  @NonNull String username;

  /**
   * Электронная почта
   */
  @JsonProperty(required = true)
  @NonNull String email;
}
