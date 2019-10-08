package ru.protei.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.protei.api.model.ApiResponse;

@Slf4j
@ControllerAdvice("ru.protei.api.controller")
public class ApplicationAdvice {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse> catchException(Exception exception) {
    log.error("Error happened during handling a request", exception);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }
}
