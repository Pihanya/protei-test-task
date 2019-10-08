package ru.protei.api.controller;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.protei.api.model.ApiResponse;
import ru.protei.api.model.OnlineStatus;
import ru.protei.api.request.CreateUserRequest;
import ru.protei.api.response.ChangeUserOnlineStatusResponse;
import ru.protei.api.response.CreateUserError;
import ru.protei.api.response.GetUserInfoResponse;
import ru.protei.api.response.GetUserInfoResponse.Error;
import ru.protei.api.service.UserService;

@RestController
@RequestMapping(path = "/users")
public class UserController {

  private UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<ApiResponse<UUID, CreateUserError>> createUser(
      @RequestBody CreateUserRequest request) {
    ApiResponse<UUID, CreateUserError> response = userService.createUser(request);
    if (response.isSuccess()) {
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } else {
      if (response.getErrorCode() == CreateUserError.EMAIL_OR_USERNAME_OCCUPIED ||
          response.getErrorCode() == CreateUserError.PHONE_NUMBER_OCCUPIED) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
      }

      return ResponseEntity.badRequest().body(response);
    }
  }

  @GetMapping(path = "/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<ApiResponse<GetUserInfoResponse, GetUserInfoResponse.Error>> getUserInfo(
      @PathVariable UUID userId) {
    ApiResponse<GetUserInfoResponse, Error> response = userService.getUserInfo(userId);
    if (response.isSuccess()) {
      return ResponseEntity.ok(response);
    } else {
      if (response.getErrorCode() == GetUserInfoResponse.Error.USER_NOT_FOUND) {
        return ResponseEntity.notFound().build();
      } else {
        return ResponseEntity.badRequest().body(response);
      }
    }
  }

  @PatchMapping(path = "/{userId}/status/{status}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<ApiResponse<ChangeUserOnlineStatusResponse, ChangeUserOnlineStatusResponse.Error>> changeUserOnlineStatus(
      @PathVariable UUID userId,
      @PathVariable String status
  ) {
    var response = userService.changeUserOnlineStatus(userId, OnlineStatus.fromCode(status));
    if (response.isSuccess()) {
      return ResponseEntity.ok(response);
    } else {
      if (response.getErrorCode() == ChangeUserOnlineStatusResponse.Error.USER_NOT_FOUND) {
        return ResponseEntity.notFound().build();
      } else {
        return ResponseEntity.badRequest().body(response);
      }
    }
  }
}
