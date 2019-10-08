package ru.protei.api.model;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;


/**
 * На любой запрос к сервису пользователю приходит в ответ объект данного класса Структура класса
 * позволяет хранить как результат выполнения запроса ({@link ApiResponse#result}), так и состояние
 * выполнения запроса: успех/неуспех ({@link ApiResponse#success}) вместе с кодом ошибки ({@link
 * ApiResponse#errorCode}) и, по необходимости, возникшим исплючением {@link ApiResponse#exception}
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Accessors(chain = true) @FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse<ResultType, ErrorCodeType> {

  /**
   * Объект результата запроса
   */
  @JsonProperty(required = true)
  ResultType result;

  /**
   * Флажок успеха/неуспеха запроса
   */
  @JsonProperty(required = true)
  boolean success;

  /**
   * Код ошибки (в случае неуспеха запроса)
   */
  @JsonProperty
  ErrorCodeType errorCode;

  /**
   * Исключение (в слючае неуспеха запроса)
   */
  @JsonIgnore
  Exception exception;

  /**
   * Успешен ли запрос
   * @return {@code true} если успешен, иначе {@code false}
   */
  public boolean isSuccess() {
    return success;
  }

  /**
   * Есть ли объект результата в ответе
   * @return {@code true} если запрос успешен и результат не {@code null}, иначе {@code false}
   */
  @JsonIgnore
  public boolean hasResult() {
    return success && result != null;
  }

  /**
   * Не успешен ли запрос. Противоположно методу {@link #isSuccess()}
   * @return {@code true} если запрос неуспешен, иначе {@code false}
   */
  @JsonIgnore
  public boolean isError() {
    return !success;
  }

  /**
   * Убедиться в успешности в запроса. В случае, если {@code success == false}, метод выбрасывает {@link RuntimeException}
   * @throws RuntimeException если запрос неуспешен
   */
  public void ensureSuccess() {
    if (!success) {
      if (exception != null) {
        throw new RuntimeException(exception);
      } else {
        throw new RuntimeException();
      }
    }
  }

  public static <ResultType, ErrorCodeType> ApiResponse<ResultType, ErrorCodeType> createSuccessResponseWith(
      ResultType result) {
    return new ApiResponse<ResultType, ErrorCodeType>()
        .setSuccess(true)
        .setResult(result);
  }

  public static <ResultType, ErrorCodeType> ApiResponse<ResultType, ErrorCodeType> createFailResponseWith(
      ErrorCodeType errorCode, Exception exception
  ) {
    checkNotNull(errorCode);
    return new ApiResponse<ResultType, ErrorCodeType>()
        .setSuccess(false)
        .setErrorCode(errorCode)
        .setException(exception);
  }

  public static <ResultType, ErrorCodeType> ApiResponse<ResultType, ErrorCodeType> createFailResponseWith(
      ErrorCodeType errorCode) {
    return createFailResponseWith(errorCode, null);
  }
}
