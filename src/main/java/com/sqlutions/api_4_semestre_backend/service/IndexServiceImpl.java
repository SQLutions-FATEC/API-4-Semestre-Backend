package com.sqlutions.api_4_semestre_backend.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sqlutions.api_4_semestre_backend.entity.Index;
import com.sqlutions.api_4_semestre_backend.entity.Radar;
import com.sqlutions.api_4_semestre_backend.entity.Reading;
import com.sqlutions.api_4_semestre_backend.repository.ReadingRepository;

@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private ReadingRepository readingRepository;

    /**
     * Calcula o índice de segurança com base em uma lista de leituras de
     * velocidade.
     * O índice é um valor inteiro de 1 a 5, onde 1 representa a melhor segurança e
     * 5 a pior.
     * 
     * O cálculo considera dois fatores principais:
     * <ul>
     * <li>A porcentagem de veículos que excederam o limite de velocidade
     * regulamentado.</li>
     * <li>A média do excesso de velocidade dos veículos que ultrapassaram o
     * limite.</li>
     * </ul>
     * 
     * Os fatores são ponderados (40% para a porcentagem de infrações e 60% para a
     * média do excesso)
     * e combinados para gerar um índice normalizado entre 1 e 5.
     * 
     * @param readings Lista de leituras de velocidade, cada uma contendo a
     *                 velocidade registrada e o limite regulamentado.
     * @return Índice de segurança calculado (1 a 5), onde valores menores indicam
     *         maior segurança.
     */
    private Integer getSecurityIndex(List<Reading> readings) {
        long totalReadings = readings.size();
        if (totalReadings == 0)
            return 1;

        long nOverLimit = readings.stream()
                .filter(r -> r.getSpeed() > r.getRadar().getRegulatedSpeed())
                .count();

        float percentageOverLimit = (nOverLimit * 100f) / totalReadings;
        double averageExcess = readings.stream()
                .filter(r -> r.getSpeed() > r.getRadar().getRegulatedSpeed())
                .mapToDouble(r -> r.getSpeed() - r.getRadar().getRegulatedSpeed())
                .average()
                .orElse(0.0);

        // Combina os fatores ponderados (40% infrações, 60% excesso)
        float overLimitWeight = 0.4f;
        float averageExcessWeight = 0.6f;
        float rawIndex = ((percentageOverLimit * overLimitWeight) + ((float) averageExcess * averageExcessWeight))
                / (overLimitWeight + averageExcessWeight);

        // Mapeia o índice para o intervalo 1–5
        int index = Math.round(1 + 4 * (rawIndex / 100));
        return Math.min(Math.max(index, 1), 5);
    }

    /**
     * Calcula o índice de tráfego com base em uma lista de leituras de velocidade.
     * 
     * O índice é determinado pela densidade de leituras (leituras por minuto) e
     * pela
     * velocidade relativa média (média das velocidades medidas / média das
     * velocidades regulamentadas).
     * 
     * A classificação é feita conforme as seguintes condições:
     * <ul>
     * <li>I_traf = 1, se D < 100 e V_rel > 70</li>
     * <li>I_traf = 2, se 100 <= D < 300 e V_rel > 60</li>
     * <li>I_traf = 3, se 300 <= D < 600 ou 40 <= V_rel <= 60</li>
     * <li>I_traf = 4, se 600 <= D ou 30 <= V_rel < 40</li>
     * <li>I_traf = 5, se V_rel < 30</li>
     * </ul>
     * 
     * @param readings Leituras associadas a um radar ou conjunto de radares
     * @return Índice de tráfego (1 a 5)
     */
    private Integer getTrafficIndex(List<Reading> readings) {
        if (readings.isEmpty())
            return 1;

        LocalDateTime maxDate = readings.stream()
                .map(Reading::getDate)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        LocalDateTime minDate = readings.stream()
                .map(Reading::getDate)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        Duration readingLength = Duration.between(minDate, maxDate);
        float readingLengthMinutes = readingLength.toSeconds() / 60.0f;
        float avgReadingsPerMinute = readings.size() / (readingLengthMinutes == 0 ? 1 : readingLengthMinutes);

        float avgSpeed = (float) readings.stream()
                .mapToInt(Reading::getSpeed)
                .average()
                .orElse(0.0);
        float avgRegSpeed = (float) readings.stream()
                .mapToInt(r -> r.getRadar().getRegulatedSpeed())
                .average()
                .orElse(0.0);
        float relativeSpeed = avgRegSpeed == 0 ? 0 : (avgSpeed / avgRegSpeed) * 100;

        // Classificação do índice de tráfego conforme as regras descritas
        if (avgReadingsPerMinute < 100 && relativeSpeed > 70)
            return 1;
        if (avgReadingsPerMinute < 300 && relativeSpeed > 60)
            return 2;
        if ((avgReadingsPerMinute < 600) || (relativeSpeed >= 40 && relativeSpeed <= 60))
            return 3;
        if (avgReadingsPerMinute >= 600 || (relativeSpeed >= 30 && relativeSpeed < 40))
            return 4;
        return 5;
    }

    /**
     * Calcula o índice geral da cidade para um determinado intervalo de tempo.
     * <p>
     * Esta função agrega diversos índices (por exemplo, tráfego, segurança) com
     * base nas leituras coletadas entre {@code timestamp - minutes} e
     * {@code timestamp}.
     * O índice resultante representa o status geral da cidade, onde valores menores
     * indicam melhores condições.
     * <p>
     * Atualmente, o cálculo é baseado nos índices de tráfego e segurança,
     * determinados pela análise das leituras no período especificado.
     * 
     * @param minutes   Número de minutos a considerar a partir do timestamp
     *                  informado.
     * @param timestamp Fim do intervalo de tempo para o cálculo do índice.
     * @return Valor inteiro representando o índice da cidade (1 a 5, onde menor é
     *         melhor).
     */
    @Override
    public Index getCityIndex(int minutes, LocalDateTime timestamp) {
        LocalDateTime end = timestamp;
        LocalDateTime start = end.minusMinutes(minutes);

        List<Reading> readings = readingRepository.findByDateBetween(start, end);
        if (readings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma leitura encontrada no período informado.");
        }

        return getIndexFromReadings(readings);
    }

    /**
     * Calcula o índice geral para um ou mais radares em um determinado intervalo de
     * tempo.
     */
    @Override
    public Index getRadarIndexes(int minutes, Radar[] radars, LocalDateTime timestamp) {
        LocalDateTime end = timestamp;
        LocalDateTime start = end.minusMinutes(minutes);

        List<Reading> readings = readingRepository.findByRadarInAndDateBetween(List.of(radars), start, end);
        if (readings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Nenhuma leitura encontrada para o radar informado.");
        }

        return getIndexFromReadings(readings);
    }

    /**
     * Calcula o índice geral de uma região específica em um determinado intervalo
     * de tempo.
     */
    @Override
    public Index getRegionIndexes(int minutes, String region, LocalDateTime timestamp) {
        LocalDateTime end = timestamp;
        LocalDateTime start = end.minusMinutes(minutes);

        List<Reading> readings = readingRepository.findByRadarAddressRegionInAndDateBetween(List.of(region), start,
                end);
        if (readings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Nenhuma leitura encontrada para a região informada.");
        }

        return getIndexFromReadings(readings);
    }

    /**
     * Calcula o índice combinado a partir de uma lista de leituras,
     * levando em conta os índices de tráfego e segurança.
     */
    @Override
    public Index getIndexFromReadings(List<Reading> readings) {
        if (readings == null || readings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A lista de leituras está vazia ou nula.");
        }

        Integer trafficIndex = getTrafficIndex(readings);
        Integer securityIndex = getSecurityIndex(readings);

        return new Index(securityIndex, trafficIndex);
    }
}
