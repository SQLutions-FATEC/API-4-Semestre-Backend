package com.sqlutions.api_4_semestre_backend.service;

import com.sqlutions.api_4_semestre_backend.entity.NotificationLog;
import com.sqlutions.api_4_semestre_backend.repository.NotificationLogRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.from-number}")
    private String fromNumber;

    @Value("${notification.to-number}")
    private String toNumber;

    private final NotificationLogRepository logRepository;

    public NotificationService(NotificationLogRepository logRepository) {
        this.logRepository = logRepository;
    }

   
    public void sendAlert(String messageText, String indexType, Integer indexValue) {
        Twilio.init(accountSid, authToken);
        boolean success = false;
        String errorDetails = null;

        try {
            Message message = Message.creator(
                    new com.twilio.type.PhoneNumber(toNumber),
                    new com.twilio.type.PhoneNumber(fromNumber),
                    messageText
            ).create();

            success = true;
            System.out.println(" Notificação enviada com sucesso: " + message.getSid());
        } catch (Exception e) {
            errorDetails = e.getMessage();
            System.err.println(" Falha ao enviar notificação: " + e.getMessage());
        }

        NotificationLog log = new NotificationLog(
                messageText,
                toNumber,
                success,
                errorDetails,
                indexType,
                indexValue
        );
        logRepository.save(log);
    }
}
