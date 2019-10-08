package ru.protei.api.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.regex.Pattern;

public class UserDataValidator {

  /**
   * Регулярное выражение для валидации электронной почты
   */
  private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
      Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


  /**
   * Регулярное выражение для валидации имени пользователя
   */
  private static final Pattern VALID_USERNAME = Pattern.compile("^[a-zA-Z0-9._-]{3,}$");

  /**
   * Регулярное выражение для валидации телефонного номера
   *
   */
  private static final Pattern VALID_PHONE_NUMBER_REGEX = Pattern.compile("^\\+[1-9][0-9]{3,14}$");


  /**
   * Разрешены английские строчные и заглавные буквы
   * @param username
   * @return
   */
  public static boolean validateUsername(String username) {
    checkNotNull(username);
    return VALID_USERNAME.matcher(username.strip()).find();
  }

  /**
   * @param phoneNumber
   * @return
   */
  public static boolean validatePhoneNumber(String phoneNumber) {
    checkNotNull(phoneNumber);
    return VALID_PHONE_NUMBER_REGEX.matcher(phoneNumber.strip()).find();
  }

  /**
   *
   * @param email
   * @return
   */
  public static boolean validateEmail(String email) {
    checkNotNull(email);
    return VALID_EMAIL_ADDRESS_REGEX.matcher(email.strip()).find();
  }
}
