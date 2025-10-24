package com.sqlutions.api_4_semestre_backend.service;

import com.sqlutions.api_4_semestre_backend.entity.Index;
import com.sqlutions.api_4_semestre_backend.entity.Reading;
import com.sqlutions.api_4_semestre_backend.event.HighIndexEvent;
import com.sqlutions.api_4_semestre_backend.repository.ReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IndexMonitorService {

    @Autowired
    private IndexServiceImpl indexService;

    @Autowired
    private ReadingRepository readingRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    
    @Scheduled(fixedRate = 300000) 
    public void monitorTrafficAndSecurity() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusMinutes(10);

        List<Reading> readings = readingRepository.findByDateBetween(start, now);

        if (readings.isEmpty()) {
            System.out.println("Sem leitura encontrada no período. Ignorando monitoramento.");
            return;
        }

        Index index = indexService.getIndexFromReadings(readings);
        int trafficIndex = index.getTrafficIndex();
        int securityIndex = index.getSecurityIndex();

        System.out.println(" Verificando níveis:");
        System.out.println(" - Nível de Tráfego: " + trafficIndex);
        System.out.println(" - Nível de Segurança: " + securityIndex);

        if (trafficIndex >= 3 || securityIndex >= 3) {
            System.out.println("Nivel crítico detectado! (Tráfego: " + trafficIndex + ", Segurança: " + securityIndex + ")");
            eventPublisher.publishEvent(new HighIndexEvent(this, index));
        }
    }
}
