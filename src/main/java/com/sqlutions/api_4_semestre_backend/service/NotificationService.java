package com.sqlutions.api_4_semestre_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sqlutions.api_4_semestre_backend.entity.NotificationLog;
import com.sqlutions.api_4_semestre_backend.repository.NotificationLogRepository;

@Service
public class NotificationService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.chat-id}")
    private String chatId;

    private final NotificationLogRepository logRepository;
    private final TimeService timeService;

    public NotificationService(NotificationLogRepository logRepository, TimeService timeService) {
        this.logRepository = logRepository;
        this.timeService = timeService;
    }

    public void sendAlert(String messageText, String indexType, Integer indexValue) {

        NotificationLog log = new NotificationLog(
                messageText,
                chatId,
                false,
                null,
                indexType,
                indexValue
        );

        log.setStartAt(timeService.getCurrentTimeClampedToDatabase());

        System.out.println("Enviando notificação...");

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

            log.setSuccess(true);

            System.out.println("✅ Notificação enviada com sucesso");
        } catch (Exception e) {
            log.setErrorDetails(e.getMessage());
            log.setSuccess(false);

            System.out.println("❌ Erro ao enviar notificação: " + e.getMessage());
        }

        log.setCompletedAt(timeService.getCurrentTimeClampedToDatabase());
        logRepository.save(log);

        System.out.println("Log salvo no banco de dados");
        System.out.println("Iniciado em: " + log.getStartAt());
        System.out.println("Finalizado em: " + log.getCompletedAt());
    }
}
