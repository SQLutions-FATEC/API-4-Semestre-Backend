package com.sqlutions.api_4_semestre_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sqlutions.api_4_semestre_backend.entity.Radar;
import com.sqlutions.api_4_semestre_backend.entity.Reading;
import com.sqlutions.api_4_semestre_backend.repository.ReadingRepository;

@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private ReadingRepository readingRepository;

    /**
     * Calcula o índice de segurança com base em uma lista de leituras de velocidade.
     * O índice é um valor inteiro de 1 a 5, onde 1 representa a melhor segurança e 5 a pior.
     * 
     * O cálculo considera dois fatores principais:
     * <ul>
     *   <li>A porcentagem de veículos que excederam o limite de velocidade regulamentado.</li>
     *   <li>A média do excesso de velocidade dos veículos que ultrapassaram o limite.</li>
     * </ul>
     * 
     * Os fatores são ponderados (40% para a porcentagem de infrações e 60% para a média do excesso)
     * e combinados para gerar um índice normalizado entre 1 e 5.
     * 
     * @param readings Lista de leituras de velocidade, cada uma contendo a velocidade registrada e o limite regulamentado.
     * @return Índice de segurança calculado (1 a 5), onde valores menores indicam maior segurança.
     */
    private Integer getSecurityIndex(List<Reading> readings) {
        // dada uma lista de leituras, calcular a porcentagem de carros que estão
        // acima da velocidade máxima
        Long totalReadings = (long) readings.size();
        System.out.println(totalReadings);
        if (totalReadings == 0) {
            return 1; // se não houver leituras, considerar que o índice é o melhor possível
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

    /**
     * Calcula o índice geral da cidade para um determinado intervalo de tempo.
     * <p>
     * Esta função agrega diversos índices (por exemplo, tráfego, segurança) com base nas leituras
     * coletadas entre {@code timestamp - minutes} e {@code timestamp}. O índice resultante
     * representa o status geral da cidade, onde valores menores indicam melhores condições.
     * <p>
     * Atualmente, o cálculo é baseado no índice de segurança, determinado pela análise
     * da porcentagem de veículos que excederam o limite de velocidade no período especificado.
     * 
     * @param minutes   número de minutos a considerar a partir do timestamp informado
     * @param timestamp fim do intervalo de tempo para o cálculo do índice
     * @return valor inteiro representando o índice da cidade (1 a 5, onde menor é melhor)
     */
    public Integer getCityIndex(int minutes, java.time.LocalDateTime timestamp) {
        // TODO: adicionar outros índices
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);
        System.out.println("Calculating city index for time range: " + timeStart + " to " + timeEnd);
        List<Reading> readings = readingRepository.findByDateBetween(timeStart, timeEnd);
        System.out.println("Reading count: " + readings.size());
        Integer securityIndex = getSecurityIndex(readings);

        return securityIndex;
    }

    @Override
    public Integer getRadarIndexes(int minutes, Radar[] radars, java.time.LocalDateTime timestamp) {
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);
        System.out.println("Calculating radar index for time range: " + timeStart + " to " + timeEnd);
        List<Reading> readings = readingRepository.findByRadarInAndDateBetween(List.of(radars), timeStart, timeEnd);
        System.out.println("Reading count: " + readings.size());
        Integer securityIndex = getSecurityIndex(readings);

        return securityIndex;
    }

    @Override
    public Integer getRegionIndexes(int minutes, String region, java.time.LocalDateTime timestamp) {
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);
        System.out.println("Calculating region index for time range: " + timeStart + " to " + timeEnd);
        List<Reading> readings = readingRepository.findByRadarAddressRegionInAndDateBetween(List.of(region), timeStart, timeEnd);
        System.out.println("Reading count: " + readings.size());
        Integer securityIndex = getSecurityIndex(readings);

        return securityIndex;
    }

}
