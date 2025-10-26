package com.sqlutions.api_4_semestre_backend.listener;

import com.sqlutions.api_4_semestre_backend.event.HighIndexEvent;
import com.sqlutions.api_4_semestre_backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class HighIndexEventListener {

    @Autowired
    private NotificationService notificationService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @EventListener
    public void handleHighIndexEvent(HighIndexEvent event) {
        var index = event.getIndex();

        int trafficIndex = index.getTrafficIndex();
        int securityIndex = index.getSecurityIndex();
        ZonedDateTime timestamp = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
        String formattedTimestamp = timestamp.format(formatter);

        if (trafficIndex >= 3) {
            String message = String.format(
                "🚦 *ALERTA DE NÍVEL CRÍTICO – TRÁFEGO* 🚦\n\n" +
                "🔹 Nível de Tráfego: *%d*\n" +
                "⚠️ Condições de congestionamento elevadas foram detectadas.\n\n" +
                "📅 Data/hora: %s",
                trafficIndex, formattedTimestamp
            );

            System.out.println(" Enviando alerta sobre tráfego...");
            notificationService.sendAlert(message, "TRAFFIC", trafficIndex);
        }

        if (securityIndex >= 3) {
            String message = String.format(
                "🚨 *ALERTA DE NÍVEL CRÍTICO – SEGURANÇA* 🚨\n\n" +
                "🔹 Nível de Segurança: *%d*\n" +
                "⚠️ Atenção: condições inseguras detectadas na via.\n\n" +
                "📅 Data/hora: %s",
                securityIndex, formattedTimestamp
            );

            System.out.println(" Enviando alerta sobre segurança...");
            notificationService.sendAlert(message, "SECURITY", securityIndex);
        }
    }
}
