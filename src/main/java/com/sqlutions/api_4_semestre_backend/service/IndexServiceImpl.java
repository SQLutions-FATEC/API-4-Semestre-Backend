package com.sqlutions.api_4_semestre_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        // dada uma lista de leituras, calcular a porcentagem de carros que estão
        // acima da velocidade máxima
        Long totalReadings = (long) readings.size();
        System.out.println(totalReadings);
        if (totalReadings == 0) {
            return 1;
        }
        Long nOverLimit = readings.stream().filter(r -> r.getSpeed() > r.getRadar().getRegulatedSpeed()).count();
        Float overLimitWeight = 0.4f; // peso da porcentagem de carros acima do limite
        Float averageExcessWeight = 0.6f; // peso da média de excesso de velocidade
        Long percentageOverLimit = (nOverLimit * 100) / totalReadings;
        System.out.println(nOverLimit + " / " + totalReadings + " = " + percentageOverLimit);

        Double averageExcess = readings.stream()
                .filter(r -> r.getSpeed() > r.getRadar().getRegulatedSpeed())
                .mapToLong(r -> r.getSpeed() - r.getRadar().getRegulatedSpeed())
                .average()
                .orElse(0.0);
        System.out.println("Average excess: " + averageExcess);
        // fórmula para calcular o índice de segurança:
        // índice = 1 + (porcentagem de carros acima do limite * peso)
        // + (média de excesso de velocidade * peso)
        Float rawIndex = ((nOverLimit * overLimitWeight) + (averageExcess.floatValue() * averageExcessWeight))
                / (overLimitWeight + averageExcessWeight);
        System.out.println("Raw index: " + rawIndex);
        Integer index = Math.round(1 + 4 * (rawIndex) / 100); // mapear para 1-5
        return index;
    }

    private Integer getTrafficIndex(List<Reading> readings) {
        // primeiro, pegar a duração total das leituras (última leitura - primeira
        // leitura)
        java.time.LocalDateTime readingLength = readings.stream()
                .map(Reading::getDate)
                .max(java.time.LocalDateTime::compareTo)
                .orElse(java.time.LocalDateTime.now())
                .minusSeconds(readings.stream()
                        .map(Reading::getDate)
                        .min(java.time.LocalDateTime::compareTo)
                        .orElse(java.time.LocalDateTime.now())
                        .toLocalTime()
                        .toSecondOfDay());
        System.out.println("Reading length: " + readingLength);
        // depois, calcular a média de leituras por minuto
        Float averageReadingsPerMinute = readings.size() / (readingLength.toLocalTime().toSecondOfDay() / 60.0f);
        System.out.println("Average readings per minute: " + averageReadingsPerMinute);

        // calcular a velocidade relativa (média de velocidade / velocidade
        // regulamentada) (%)
        Float averageSpeed = (float) readings.stream()
                .mapToInt(Reading::getSpeed)
                .average()
                .orElse(0.0);
        Float averageRegulatedSpeed = (float) readings.stream()
                .mapToInt(r -> r.getRadar().getRegulatedSpeed())
                .average()
                .orElse(0.0);
        Float relativeSpeed = (averageSpeed / averageRegulatedSpeed) * 100;
        System.out.println("Relative speed: " + relativeSpeed);

        // classificar o índice:
        // I_traf = 1, se D < 100 e V_rel > 70
        // I_traf = 2, se 100 <= D < 300 e V_rel > 60
        // I_traf = 3, se 300 <= D < 600 ou 40 <= V_rel <= 60
        // I_traf = 4, se 600 <= D ou 30 <= V_rel < 40
        // I_traf = 5, se V_rel < 30
        Integer index;
        if (averageReadingsPerMinute < 100 && relativeSpeed > 70) {
            index = 1;
        } else if (averageReadingsPerMinute >= 100 && averageReadingsPerMinute < 300 && relativeSpeed > 60) {
            index = 2;
        } else if ((averageReadingsPerMinute >= 300 && averageReadingsPerMinute < 600)
                || (relativeSpeed >= 40 && relativeSpeed <= 60)) {
            index = 3;
        } else if (averageReadingsPerMinute >= 600 || (relativeSpeed >= 30 && relativeSpeed < 40)) {
            index = 4;
        } else { // relativeSpeed < 30
            index = 5;
        }
        return index;
    }

    /**
     * Calcula o índice geral da cidade para um determinado intervalo de tempo.
     * <p>
     * Esta função agrega diversos índices (por exemplo, tráfego, segurança) com
     * base nas leituras
     * coletadas entre {@code timestamp - minutes} e {@code timestamp}. O índice
     * resultante
     * representa o status geral da cidade, onde valores menores indicam melhores
     * condições.
     * <p>
     * Atualmente, o cálculo é baseado no índice de segurança, determinado pela
     * análise
     * da porcentagem de veículos que excederam o limite de velocidade no período
     * especificado.
     * 
     * @param minutes   número de minutos a considerar a partir do timestamp
     *                  informado
     * @param timestamp fim do intervalo de tempo para o cálculo do índice
     * @return valor inteiro representando o índice da cidade (1 a 5, onde menor é
     *         melhor)
     */
    public Index getCityIndex(int minutes, java.time.LocalDateTime timestamp) {
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);
        System.out.println("Calculating city index for time range: " + timeStart + " to " + timeEnd);
        List<Reading> readings = readingRepository.findByDateBetween(timeStart, timeEnd);
        System.out.println("Reading count: " + readings.size());
        Index index = getIndexFromReadings(readings);
        return index;
    }

    @Override
    public Index getIndexFromReadings(List<Reading> readings) {
        if (readings == null || readings.isEmpty()) {
            throw new IllegalArgumentException("Readings list is null or empty");
        }
        Integer trafficIndex = getTrafficIndex(readings);
        Integer securityIndex = getSecurityIndex(readings);

        return new Index(securityIndex, trafficIndex);
    }

    @Override
    public Index getRadarIndexes(int minutes, Radar[] radars, java.time.LocalDateTime timestamp) {
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);
        System.out.println("Calculating radar index for time range: " + timeStart + " to " + timeEnd);

        List<Reading> readings = readingRepository.findByRadarInAndDateBetween(List.of(radars), timeStart, timeEnd);
        System.out.println("Reading count: " + readings.size());

        Index index = getIndexFromReadings(readings);
        return index;
    }

    @Override
    public Index getRegionIndexes(int minutes, String region, java.time.LocalDateTime timestamp) {
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);
        System.out.println("Calculating region index for time range: " + timeStart + " to " + timeEnd);

        List<Reading> readings = readingRepository.findByRadarAddressRegionInAndDateBetween(List.of(region), timeStart,
                timeEnd);
        System.out.println("Reading count: " + readings.size());

        Index index = getIndexFromReadings(readings);

        return index;
    }
}
