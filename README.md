# VK Bot Controller

Этот репозиторий содержит реализацию контроллера Spring Boot, который обрабатывает запросы от Callback API ВКонтакте (VK). `VkBotController` предназначен для обработки входящих событий, таких как новые сообщения, и отправки ответов пользователям.

### Требования

- Java 11 или выше
- Maven
- Spring Boot
- [ngrok](https://ngrok.com/) (для проброса локального сервера в интернет)

## Особенности

- Обрабатывает различные типы событий от Callback API VK, включая:
  - Запросы на подтверждение
  - Новые сообщения
  - Состояние набора текста, ответы на сообщения и прочтение сообщений
- Отправляет ответы пользователям на основе их сообщений.
- Использует RestTemplate для взаимодействия с API VK для отправки сообщений.

## Начало работы

### Клонируйте репозиторий

```bash
git clone https://github.com/2desoo/VKbot.git
cd VKbot
```
## Настройка applicatiom.properties
- vk.api.token=ВАШ_VK_API_ТОКЕН
- vk.confirmation.code=ВАШ_КОД_ПОДТВЕРЖДЕНИЯ
- vk.group.id=ВАШ_GROUP_ИД

## Использование
Запустите приложение с помощью вашей IDE или выполните команду:

```bash
mvn spring-boot:run
```
Запустите ngrok для проброса локального сервера:

```bash
ngrok http 8080
```
Это создаст публичный URL, который будет перенаправлять на ваш локальный сервер.

Настройте ваш Callback API ВКонтакте, указав URL, предоставленный ngrok (например, http://12345678.ngrok.io/callback).

После этого ваше приложение начнет обрабатывать события от ВКонтакте.

## Пример
Когда пользователь отправляет новое сообщение, ваше приложение автоматически ответит ему, отправив сообщение "Вы сказали: <текст сообщения>".