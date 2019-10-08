package ru.protei.model;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/**
 * Детализированная информация о пользователе. Здесь есть все, что не попало в {@link ServiceUser}
 */
@Entity
@Data @EqualsAndHashCode(callSuper = true)
@Accessors(chain = true) @FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetails extends Timestampable {

  /**
   * Уникальный идентификатор пользователя
   */
  @Id
  @Column(unique = true, nullable = false, updatable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  UUID userId;

  /**
   * Экранное имя пользователя. Например ФИО
   */
  @Column(nullable = false)
  String screenName;

  /**
   * Описание пользователя
   */
  @Column
  String bio;

  /**
   * Номер телефона пользователя (уникален)
   */
  @Column(unique = true)
  String phoneNumber;


  /**
   * Ссылка на основные данные о пользователе
   */
  @OneToOne @MapsId
  ServiceUser user;
}
