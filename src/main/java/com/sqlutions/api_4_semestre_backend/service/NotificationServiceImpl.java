package com.sqlutions.api_4_semestre_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sqlutions.api_4_semestre_backend.entity.NotificationLog;
import com.sqlutions.api_4_semestre_backend.repository.NotificationLogRepository;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.chat-id}")
    private String chatId;

    @Autowired
    private NotificationLogRepository logRepository;

    @Autowired
    private TimeService timeService;

    @Override
    public void sendAlert(String reportText, String indexType, Integer indexValue) {

        NotificationLog log = new NotificationLog();

        log.setReportText(reportText);
        log.setMessageText("Notificação Automática");
        log.setIndexType(indexType);
        log.setIndexValue(indexValue);

        log.setEmissionDate(timeService.getCurrentTimeClampedToDatabase());

        System.out.println("📡 Enviando notificação...");

        try {
            String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

            String json = String.format(
                "{\"chat_id\": \"%s\", \"text\": \"%s\", \"parse_mode\": \"Markdown\"}",
                chatId,
                reportText
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(json, headers);

            new RestTemplate().postForObject(url, request, String.class);

            System.out.println("✅ Notificação enviada com sucesso");

        } catch (Exception e) {
            System.out.println("❌ Erro ao enviar notificação: " + e.getMessage());
            e.printStackTrace();
        }

        // sempre salva no banco, mesmo se o Telegram falhar
        log.setCompletionDate(timeService.getCurrentTimeClampedToDatabase());
        logRepository.save(log);

        System.out.println("📄 Log salvo no banco");
        System.out.println("Iniciado em: " + log.getEmissionDate());
        System.out.println("Finalizado em: " + log.getCompletionDate());
    }
}
