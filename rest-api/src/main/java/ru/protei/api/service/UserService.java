package ru.protei.api.service;

import java.util.UUID;
import ru.protei.api.model.ApiResponse;
import ru.protei.api.model.OnlineStatus;
import ru.protei.api.request.CreateUserRequest;
import ru.protei.api.response.ChangeUserOnlineStatusResponse;
import ru.protei.api.response.CreateUserError;
import ru.protei.api.response.GetUserInfoResponse;

/**
 * Сервис для взаимодействия с данными пользователей
 */
public interface UserService {

  /**
   * Создать пользователя с указанными данными.
   * <p>
   * Обратите внимание, что идентификатор пользователя в {@link CreateUserRequest#getUser()}
   * передавать не нужно
   *
   * @param request объект запроса
   * @return ответ API с идентификатором, присвоенным
   */
  ApiResponse<UUID, CreateUserError> createUser(CreateUserRequest request);

  /**
   * Получить информацию о пользователе с идентификатором {@code userId}
   *
   * @param userId идентификатор пользователя
   * @return ответ API с информацией о пользователе
   */
  ApiResponse<GetUserInfoResponse, GetUserInfoResponse.Error> getUserInfo(UUID userId);


  /**
   * Изменить статус пользователя с идентификатором {@code userId} на {@code onlineStatus}
   *
   * @param userId идентификатор пользователя
   * @param onlineStatus статус, который необходимо присвоить пользователю
   * @return ответ API с информацией о предыдущем и текущем состоянии пользователя, а также с
   * идентификатором пользователя
   */
  ApiResponse<ChangeUserOnlineStatusResponse, ChangeUserOnlineStatusResponse.Error> changeUserOnlineStatus(
      UUID userId, OnlineStatus onlineStatus);
}
