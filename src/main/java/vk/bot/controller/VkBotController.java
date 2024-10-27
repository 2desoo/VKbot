package vk.bot.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

/**
 * Контроллер для обработки запросов от сервера Callback API ВКонтакте.
 * Этот класс управляет обработкой входящих событий от VK Callback API,
 * таких как новые сообщения и другие типы уведомлений.
 */
@RestController
@RequestMapping("/callback")
public class VkBotController {

    private static final Logger logger = LoggerFactory.getLogger(VkBotController.class);

    @Value("${vk.api.token}")
    private String vkApiToken;

    @Value("${vk.confirmation.code}")
    private String confirmationCode;

    /**
     * Основной метод для обработки входящих запросов от сервера Callback API ВКонтакте.
     *
     * @param body тело запроса в формате JSON, содержащий данные о событии.
     * @return Ответ в формате {@link ResponseEntity} с текстом "ok" при успешной обработке
     * или статусом ошибки при возникновении исключений.
     */
    @PostMapping
    public ResponseEntity<String> handleCallback(@RequestBody String body) {

        JSONObject json;
        try {
            json = new JSONObject(body);
        } catch (JSONException e) {
            logger.error("Ошибка при разборе JSON: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON format");
        }

        String type = json.optString("type");

        // Обработка события подтверждения
        if ("confirmation".equals(type)) {
            logger.info("Получен запрос на подтверждение");
            return ResponseEntity.ok(confirmationCode);
        }

        switch (type) {
            case "message_new" -> handleNewMessage(json);
            case "message_typing_state", "message_reply", "message_read" ->
                    logger.debug("Событие типа {} обработано", type);
            default -> {
                logger.warn("Неправильный или неподдерживаемый тип запроса: {}", type);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Неправильный запрос");
            }
        }

        return ResponseEntity.ok("ok");
    }

    /**
     * Обрабатывает событие нового сообщения и отправляет ответ пользователю.
     *
     * @param json JSON-объект с информацией о новом сообщении.
     */
    private void handleNewMessage(JSONObject json) {
        JSONObject object = json.optJSONObject("object");
        if (object != null) {
            JSONObject message = object.optJSONObject("message");
            if (message != null) {
                String userId = message.optString("from_id");
                String text = message.optString("text");
                logger.info("Новое сообщение от пользователя {}: {}", userId, text);
                sendMessage(userId, text);
            }
        }
    }

    /**
     * Метод для отправки сообщения пользователю.
     *
     * @param userId Идентификатор пользователя, которому будет отправлено сообщение.
     * @param text   Сообщение, которое будет отправлено.
     */
    public void sendMessage(String userId, String text) {
        String url = "https://api.vk.com/method/messages.send";
        long randomId = new Random().nextLong();
        String messageText = "Вы сказали: " + text;

        String params = String.format(
                "user_id=%s&message=%s&access_token=%s&random_id=%d&v=5.199",
                userId, messageText, vkApiToken, randomId);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url + "?" + params,
                    HttpMethod.GET,
                    null,
                    String.class
            );
            logger.info("Сообщение успешно отправлено: {}", response.getBody());
        } catch (Exception e) {
            logger.error("Ошибка при отправке сообщения: ", e);
        }
    }
}
