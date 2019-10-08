package ru.protei.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Утилитарный класс для того, чтобы осуществлять запись времени создания сущности и время её
 * последнего измененения, а также подсчет количества изменений сущности
 */
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Data @FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class Timestampable implements Serializable {

  /**
   * Дата создания записи
   */
  @Column(updatable = false)
  LocalDateTime createDate;

  /**
   * Дата последнего изменения записи
   */
  @Column
  LocalDateTime editDate;

  /**
   * Количество изменений записи
   */
  @Version
  @Column
  Integer version;

  /**
   * Обновить дату последнего изменения и выполнить инкремент версии
   */
  public void update() {
    updatedAt();
  }

  @PrePersist
  void createdAt() {
    this.createDate = this.editDate = LocalDateTime.now();
    this.version = 0;
  }

  @PreUpdate
  void updatedAt() {
    this.editDate = LocalDateTime.now();
    this.version += 1;
  }
}

