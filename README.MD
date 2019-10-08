# Приложение User Service
***
- Добавление пользователя
- Получение состояние и информации о пользователе
- Работа с состоянием пользователя


## 1. Способ взаимодействия с приложением
* HTTP-запросы в рамках специфицированного REST API

## 2. Использованные технологии:
1. Язык программирования - [Java 11](https://openjdk.java.net/projects/jdk/11/)
2. Конфигурация приложения и управление бинами - [Spring Boot](https://spring.io/projects/spring-boot)
3. Взаимодействие с БД - [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
4. Утилитарная библиотека - [Guava](https://github.com/google/guava)
5. JSON-маппинг - [Jackson](https://github.com/FasterXML/jackson)
6. Автогенерация методов/классов/... - [Lombok](https://projectlombok.org/)
7. Тестирование - [JUnit 5](https://junit.org/junit5/)
8. Автоматизация сборки - [Gradle 5.6.2](https://docs.gradle.org/5.6.2/release-notes.html)

## 3. Запуск приложения
1. Распакуйте архив с JAR-файлом, который находится в директории artifacts/
2. Откройте консоль и с помощью неё перейдите в директорию, где находится JAR-файл
3. Убедитесь, что у вас стоит подходящая версия Java (`java -version`). Должна быть 11-ая и выше
4. Введите `java -jar somejar.jar`

## 4. Параметры среды


| Параметр        | Возможные значения           | Что делает  |
| ------------- |:-------------:| ------:|
| spring.profiles.active      			 | test | приложение будет использовать in-memory базу данных |
| server.port      | 1000-65535     |   порт на котором сервер принимает запросы |
| server.context-path        			| /v1/api (по-умолчанию)           | установить префикс для диспетчера запросов  |
| service.online-status-timeout 			| [1s-60s]/[1m-60m]      |    задержка перед тем, как пользователь переходит в **AWAY** |
| spring.datasource.url        			| URL           | установить URL базы данных  |
| spring.datasource.username        |   char[]         | установить логин для авторизации в базе данных  |
| spring.datasource.password        | chars[]           | установить пароль для авторизации в базе данных  |
| spring.datasource.platform        | postgresql/h2           | установить платформу  |
| jpa.database-platform        			| dialect(см.ниже)           | установить диалект  |



**org.hibernate.dialect.H2Dialect** - диалект для H2

**org.hibernate.dialect.PostgreSQLDialect** - диалект для PostgreSQL

## 5. Функционал
### Добавление нового пользователя
**Сигнатура:**


```
createUser(user: User, userInfo: UserInfo) -> <id: UUID>
```

**Формат запроса:**


```
POST /v1/api/users HTTP/1.1
Content-Type: application/json
{
	"user": {
		"username": "{username}",
		"email": "{email}"
	},
	"userInfo": {
		"phoneNumber": "{phoneNumber}",
		"screenName": "{screenName}"
	}
}
```

**Фомат ответа:**


```
HTTP/1.1 201
{
  "result": "{id}",
  "success": true
}
```


### Получение информации о пользователе
**Сигнатура:**


```
getUserInfo(id: UUID) -> <user: User, userInfo: UserInfo, onlineStatus: OnlineStatus>
```

**Шаблон запроса:**


```
GET /v1/api/users/{id} HTTP/1.1
```

**Шаблон успешного ответа:**


```
{
  "result": {
    "user": {
      "id": "{id}",
      "username": "{username}",
      "email": "{email}"
    },
    "userInfo": {
      "phoneNumber": "{phoneNumber}",
      "screenName": "{screenName}",
      "registerDate": "{registerDate}"
    },
    "onlineStatus": "{onlineStatus}"
  },
  "success": true
}
```


### Изменения статуса пользователя (online/away/offline)
**Сигнатура:**


```
changeUserOnlineStatus(id: UUID, status: OnlineStatus) -> <id: UUID, beforeStatus: OnlineStatus, nowStatus: OnlineStatus>
```

**Шаблон запроса:**


```
PATCH /v1/api/users/{id}/status/{status} HTTP/1.1
```

**Шаблон ответа:**


```
HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
{
  "result": {
    "userId": "{id}",
    "beforeStatus": "{beforeStatus}",
    "nowStatus": "{nowStatus}"
  },
  "success": true
}
```


#### Шаблон неуспешного ответа во всех запросах:
```
{
  "success": {true/false},
  "errorCode": {errorCode}
}
```
