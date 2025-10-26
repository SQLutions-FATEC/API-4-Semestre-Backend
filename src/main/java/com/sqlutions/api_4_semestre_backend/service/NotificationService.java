package com.sqlutions.api_4_semestre_backend.service;

import com.sqlutions.api_4_semestre_backend.entity.NotificationLog;
import com.sqlutions.api_4_semestre_backend.repository.NotificationLogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    private final NotificationLogRepository logRepository;

    public NotificationService(NotificationLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void sendAlert(String messageText, String indexType, Integer indexValue) {
        boolean success = false;
        String errorDetails = null;

        try {
            String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

            String json = String.format(
                    "{\"chat_id\": \"%s\", \"text\": \"%s\", \"parse_mode\": \"Markdown\"}",
                    chatId, messageText
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(json, headers);
            new RestTemplate().postForObject(url, request, String.class);

            success = true;
            System.out.println("✅ Notificação enviada via Telegram com sucesso!");
        } catch (Exception e) {
            errorDetails = e.getMessage();
            System.err.println("❌ Falha ao enviar notificação via Telegram: " + errorDetails);
        }

        NotificationLog log = new NotificationLog(
                messageText,
                chatId,
                success,
                errorDetails,
                indexType,
                indexValue
        );
        logRepository.save(log);
    }
}
