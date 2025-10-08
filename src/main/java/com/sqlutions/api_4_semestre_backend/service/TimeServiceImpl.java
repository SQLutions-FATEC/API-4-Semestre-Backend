package com.sqlutions.api_4_semestre_backend.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sqlutions.api_4_semestre_backend.repository.ReadingRepository;

import jakarta.annotation.PostConstruct;

@Service
public class TimeServiceImpl implements TimeService {

    @Autowired
    private ReadingRepository readingRepository;

    private java.time.LocalDateTime DATABASE_TIME_START;
    private java.time.LocalDateTime DATABASE_TIME_END;

    @PostConstruct
    private void initializeDatabaseTimes() {
        DATABASE_TIME_START = readingRepository.findMinDate();
        DATABASE_TIME_END = readingRepository.findMaxDate();
    }

    /**
     * Retorna o horário atual mapeado para o intervalo de tempo definido pelo banco de dados.
     * 
     * Esta função obtém o horário atual do sistema e o "clampa" (mapeia ciclicamente) para o intervalo
     * entre {@code DATABASE_TIME_START} e {@code DATABASE_TIME_END}. O horário resultante é calculado
     * usando o tempo decorrido desde o epoch (1970-01-01T00:00:00) e o intervalo do banco de dados,
     * garantindo que o valor retornado sempre esteja dentro dos limites definidos.
     *
     * @return O horário atual mapeado para o intervalo do banco de dados.
     */
    @Override
    public java.time.LocalDateTime getCurrentTimeClampedToDatabase() {
        java.time.LocalDateTime currentDate = java.time.LocalDateTime.now();

        // Obter a duração total do intervalo de tempo do banco de dados
        java.time.Duration databaseDuration = java.time.Duration.between(DATABASE_TIME_START, DATABASE_TIME_END);

        // Obter a duração de um epoch fixo até o tempo atual (para garantir que mude
        // com o tempo)
        java.time.LocalDateTime epoch = java.time.LocalDateTime.of(1970, 1, 1, 0, 0);
        java.time.Duration currentDuration = java.time.Duration.between(epoch, currentDate);

        // Calcular o restante: currentDuration % databaseDuration
        long remainderNanos = currentDuration.toNanos() % databaseDuration.toNanos();
        java.time.Duration remainderDuration = java.time.Duration.ofNanos(remainderNanos);

        // Adicionar o resto ao início do tempo do banco de dados para obter o tempo mapeado
        java.time.LocalDateTime mappedDate = DATABASE_TIME_START.plus(remainderDuration);

        return mappedDate;
    }

    @Override
    public LocalDateTime getStartTime() {
        return DATABASE_TIME_START;
    }

    @Override
    public LocalDateTime getEndTime() {
        return DATABASE_TIME_END;
    }

}
