package ru.protei.model;

import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/**
 * Основная информация о пользователе: его идентификатор, имя и почта, а также статус
 */
@Entity
@Table(indexes = {@Index(name = "Index_ServiceUserId", columnList = "Id", unique = true)})
@Data @Accessors(chain = true) @FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceUser {

  /**
   * Уникальный идентификатор пользователя
   */
  @Id
  @Column(unique = true, nullable = false, updatable = false)
  UUID id;

  /**
   * Имя пользователя (уникальное)
   */
  @Column(unique = true, nullable = false, updatable = false)
  String username;

  /**
   * Электронная почта (уникальная)
   */
  @Column(unique = true, nullable = false)
  String email;

  /**
   * Статус пользователя
   */
  @Enumerated
  @Column(nullable = false)
  OnlineStatus onlineStatus;

  /**
   * Время, когда пользователь в последний раз пребывал в состоянии {@link OnlineStatus#ONLINE онлайн}
   */
  @Column
  Long lastOnlineMillis;

  /**
   * Детализировнная информация о пользователе
   */
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  UserDetails details;

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
  }
}
