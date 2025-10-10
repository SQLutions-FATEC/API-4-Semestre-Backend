package com.sqlutions.api_4_semestre_backend.service;

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
        Float percentageOverLimit = (nOverLimit * 100f) / totalReadings;
        System.out.println(nOverLimit + " / " + totalReadings + " = " + percentageOverLimit);

        Double averageExcess = readings.stream()
                .filter(r -> r.getSpeed() > r.getRadar().getRegulatedSpeed())
                .mapToLong(r -> r.getSpeed() - r.getRadar().getRegulatedSpeed())
                .average()
                .orElse(0.0);
        System.out.println("Average excess: " + averageExcess);
        
        Float rawIndex = ((percentageOverLimit * overLimitWeight) + (averageExcess.floatValue() * averageExcessWeight))
                / (overLimitWeight + averageExcessWeight);
        System.out.println("Raw index: " + rawIndex + "%");
        Integer index = Math.round(1 + 4 * (rawIndex / 100)); // mapear para 1-5
        return index;
    }

    private Integer getTrafficIndex(List<Reading> readings) {
        // primeiro, pegar a duração total das leituras (última leitura - primeira leitura)
        java.time.LocalDateTime maxDate = readings.stream()
                .map(Reading::getDate)
                .max(java.time.LocalDateTime::compareTo)
                .orElse(java.time.LocalDateTime.now());
        java.time.LocalDateTime minDate = readings.stream()
                .map(Reading::getDate)
                .min(java.time.LocalDateTime::compareTo)
                .orElse(java.time.LocalDateTime.now());
        java.time.Duration readingLength = java.time.Duration.between(minDate, maxDate);
        float readingLengthMinutes = readingLength.toSeconds() / 60.0f;
        System.out.println("Reading start: " + minDate);
        System.out.println("Reading end: " + maxDate);
        System.out.println("Reading length: " + readingLengthMinutes + " minutes");
        // depois, calcular a média de leituras por minuto
        Float averageReadingsPerMinute = readings.size() / (readingLengthMinutes == 0 ? 1 : readingLengthMinutes);
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
        System.out.println("Relative speed: " + relativeSpeed + "%");

        // classificar o índice:
        // I_traf = 1, se D < 100 e V_rel > 70
        // I_traf = 2, se 100 <= D < 300 e V_rel > 60
        // I_traf = 3, se 300 <= D < 600 ou 40 <= V_rel <= 60
        // I_traf = 4, se 600 <= D ou 30 <= V_rel < 40
        // I_traf = 5, se V_rel < 30
        
        // TODO: apresentar pontos futuros:
        // considerar o pico do radar como limite para densidade (comparar ao valor de D):
        // quanto mais perto do pico do radar, pior o índice.
        // considerar também o horário do dia (ex: 7-9 e 17-19 são piores)
        
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
    public List<Index> getCityIndex(int minutes, java.time.LocalDateTime timestamp) {
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);
        System.out.println("Calculating city index for time range: " + timeStart + " to " + timeEnd);
        List<Reading> readings = readingRepository.findByDateBetween(timeStart, timeEnd);
        System.out.println("Reading count: " + readings.size());
        // get dates of first and last reading
        java.time.LocalDateTime firstReadingDate = readings.stream()
                .map(Reading::getDate)
                .min(java.time.LocalDateTime::compareTo)
                .orElse(null);
        java.time.LocalDateTime lastReadingDate = readings.stream()
                .map(Reading::getDate)
                .max(java.time.LocalDateTime::compareTo)
                .orElse(null);
        System.out.println("First reading date: " + firstReadingDate);
        System.out.println("Last reading date: " + lastReadingDate);
        List<Index> index = getIndexFromReadings(readings);
        return index;
    }

    /**
     * Calculates an aggregated {@link Index} from a list of {@link Reading} objects by grouping them
     * according to the total period covered by the readings.
     * <p>
     * Grouping rules:
     * <ul>
     *   <li>If the period is less than 1 hour, readings are grouped by 10-minute intervals.</li>
     *   <li>If the period is less than 1 day, readings are grouped by hour.</li>
     *   <li>If the period is less than 7 days, readings are grouped by day.</li>
     * </ul>
     * <p>
     * For each group, the method calculates a traffic index and a security index, then aggregates
     * these indexes across all groups to produce a final {@link Index} result.
     * <p>
     * Special handling is performed for broken time series to ensure the first and last groups
     * are properly clamped to the available data range.
     *
     * @param readings the list of {@link Reading} objects to process; must not be {@code null} or empty,
     *                 and must contain at least two readings
     * @return an aggregated {@link Index} representing the sum of indexes calculated for each group
     * @throws ResponseStatusException if the readings list is {@code null}, empty, or contains fewer than two readings
     */
    @Override
    public List<Index> getIndexFromReadings(List<Reading> readings) {
        if (readings == null || readings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Readings list is null or empty");
        }
        // if period is less than 1 hour, group by 10 minutes
        // if period is more than 12 hours, group by hour
        // if period is more than 7 days, group by day

        // how to process broken time series:
        // clamp the first and last reading to be the same size
        // example: lastTime is 10:30 and we'd like the last 12 hours
        // then we should get from 10:00-10:30, and from then backwards each hour (9:00-10:00, 8:00-9:00, etc.)

        // for 10 minute group, same goes:
        // if lastTime is 10:35, then we should get from 10:30-10:35, and from then backwards each 10 minutes (10:20-10:30, 10:10-10:20, etc.)
        
        // for daily group, same goes:
        // if lastTime is 2023-10-01 10:35, then we should get from 2023-10-01 00:00 to 2023-10-01 10:35, and from then backwards each day (2023-09-30 00:00 to 2023-10-01 00:00)

        if (readings.size() < 2) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not enough readings to calculate index");
        }

        // find the sample period
        java.time.LocalDateTime firstReadingDate = readings.stream()
                .map(Reading::getDate)
                .min(java.time.LocalDateTime::compareTo)
                .orElse(null);
        java.time.LocalDateTime lastReadingDate = readings.stream()
                .map(Reading::getDate)
                .max(java.time.LocalDateTime::compareTo)
                .orElse(null);
        List<List<Reading>> groupedReadings = new java.util.ArrayList<>();
        java.time.Duration samplePeriod = java.time.Duration.between(firstReadingDate, lastReadingDate);
        if (samplePeriod.compareTo(java.time.Duration.ofHours(1)) < 0) {
            System.out.println("Sample period is less than 1 hour, grouping by 10 minutes");
            readings.sort((r1, r2) -> r1.getDate().compareTo(r2.getDate()));
            groupedReadings = new java.util.ArrayList<>(
                readings.stream()
                    .collect(java.util.stream.Collectors.groupingBy(r -> 
                        r.getDate().withMinute((r.getDate().getMinute() / 10) * 10).withSecond(0).withNano(0)
                    ))
                    .values()
            );
        } else if (samplePeriod.compareTo(java.time.Duration.ofDays(1)) < 0) {
            System.out.println("Sample period is less than 12 hours, grouping by hour");
            readings.sort((r1, r2) -> r1.getDate().compareTo(r2.getDate()));
            groupedReadings = new java.util.ArrayList<>(
                readings.stream()
                    .collect(java.util.stream.Collectors.groupingBy(r -> 
                        r.getDate().withMinute(0).withSecond(0).withNano(0).withHour(r.getDate().getHour())
                    ))
                    .values()
            );
        } else if (samplePeriod.compareTo(java.time.Duration.ofDays(7)) < 0) {
            System.out.println("Sample period is less than 7 days, grouping by day");
            readings.sort((r1, r2) -> r1.getDate().compareTo(r2.getDate()));
            groupedReadings = new java.util.ArrayList<>(
                readings.stream()
                    .collect(java.util.stream.Collectors.groupingBy(r -> 
                        r.getDate().withHour(0).withMinute(0).withSecond(0).withNano(0)
                    ))
                    .values()
            );
        }

        // now, for each reading group, calculate the indexes and add them to the output index list
        System.out.println("Calculating indexes from readings");
        List<Index> output = groupedReadings.stream()
            .map(group -> {
                Integer trafficIndex = getTrafficIndex(group);
                Integer securityIndex = getSecurityIndex(group);
                return new Index(securityIndex, trafficIndex, group.get(0).getDate(), group.get(group.size() - 1).getDate());
            })
            .toList();
        System.out.println("Calculated " + output.size() + " indexes from readings");
        // return the indexes grouped by time
        return groupedReadings.stream()
            .map(group -> {
                Integer trafficIndex = getTrafficIndex(group);
                Integer securityIndex = getSecurityIndex(group);
                return new Index(securityIndex, trafficIndex, group.get(0).getDate(), group.get(group.size() - 1).getDate());
            }).toList();
    }

    @Override
    public List<Index> getRadarIndexes(int minutes, Radar[] radars, java.time.LocalDateTime timestamp) {
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);
        System.out.println("Calculating radar index for time range: " + timeStart + " to " + timeEnd);

        List<Reading> readings = readingRepository.findByRadarInAndDateBetween(List.of(radars), timeStart, timeEnd);
        System.out.println("Reading count: " + readings.size());

        List<Index> index = getIndexFromReadings(readings);
        return index;
    }

    @Override
    public List<Index> getRegionIndexes(int minutes, String region, java.time.LocalDateTime timestamp) {
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);
        System.out.println("Calculating region index for time range: " + timeStart + " to " + timeEnd);

        List<Reading> readings = readingRepository.findByRadarAddressRegionInAndDateBetween(List.of(region), timeStart,
                timeEnd);
        System.out.println("Reading count: " + readings.size());

        List<Index> index = getIndexFromReadings(readings);

        return index;
    }
}
