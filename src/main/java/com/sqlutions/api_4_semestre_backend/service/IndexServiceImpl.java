package com.sqlutions.api_4_semestre_backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sqlutions.api_4_semestre_backend.entity.Index;
import com.sqlutions.api_4_semestre_backend.entity.Reading;
import com.sqlutions.api_4_semestre_backend.entity.ReadingInformation;
import com.sqlutions.api_4_semestre_backend.entity.Region;
import com.sqlutions.api_4_semestre_backend.entity.RegionMap;
import com.sqlutions.api_4_semestre_backend.repository.ReadingRepository;
import com.sqlutions.api_4_semestre_backend.repository.RegionRepository;

/**
 * Implementação do serviço responsável pelo cálculo dos índices de segurança e tráfego
 * com base nas leituras de velocidade dos radares.
 * 
 * <p>
 * Esta classe fornece métodos para calcular diferentes tipos de índices (segurança, tráfego,
 * índices por radar, região e cidade) a partir de listas de leituras ({@link Reading}).
 * Os índices são utilizados para avaliar as condições de segurança viária e fluxo de tráfego
 * em diferentes contextos e intervalos de tempo.
 * </p>
 * 
 * <p>
 * Principais funcionalidades:
 * <ul>
 *   <li>Cálculo do índice de segurança, considerando a porcentagem de veículos acima do limite e o excesso médio de velocidade.</li>
 *   <li>Cálculo do índice de tráfego, levando em conta a densidade de leituras por minuto e a velocidade relativa dos veículos.</li>
 *   <li>Geração de índices agregados para cidade, regiões ou radares específicos, em intervalos de tempo definidos.</li>
 *   <li>Agrupamento de leituras para análise temporal e geração de séries históricas de índices.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Os índices variam de 1 a 5, onde valores menores indicam melhores condições de segurança e tráfego.
 * </p>
 * 
 * <p>
 * Pontos de melhoria futuros:
 * <ul>
 *   <li>Considerar horários de pico e capacidade máxima dos radares para refinar o cálculo dos índices.</li>
 *   <li>Implementar métodos para geração de séries temporais dos índices.</li>
 * </ul>
 * </p>
 * 
 * @author Gabriel Vasconcelos
 * @see IndexService
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private ReadingRepository readingRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private ReadingService readingService;

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
    
    // TODO: apresentar pontos futuros:
    // considerar o pico do radar como limite para densidade (comparar ao valor de
    // D):
    // quanto mais perto do pico do radar, pior o índice.
    // considerar também o horário do dia (ex: 7-9 e 17-19 são piores)
    /**
     * Calcula o índice de tráfego com base em uma lista de leituras de velocidade.
     *
     * <p>
     * O índice é determinado a partir dos seguintes critérios:
     * <ul>
     *   <li>Primeiro, calcula a duração total das leituras (diferença entre a última e a primeira leitura).</li>
     *   <li>Em seguida, calcula a média de leituras por minuto.</li>
     *   <li>Calcula a velocidade relativa, que é a razão entre a média das velocidades registradas e a velocidade regulamentada do radar, em porcentagem.</li>
     *   <li>Classifica o índice de tráfego (I_traf) conforme as regras:
     *     <ul>
     *       <li>I_traf = 1, se leituras por minuto &lt; 100 e velocidade relativa &gt; 70%</li>
     *       <li>I_traf = 2, se 100 ≤ leituras por minuto &lt; 300 e velocidade relativa &gt; 60%</li>
     *       <li>I_traf = 3, se 300 ≤ leituras por minuto &lt; 600 ou 40% ≤ velocidade relativa ≤ 60%</li>
     *       <li>I_traf = 4, se leituras por minuto ≥ 600 ou 30% ≤ velocidade relativa &lt; 40%</li>
     *       <li>I_traf = 5, se velocidade relativa &lt; 30%</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * <p>
     * Pontos futuros a serem considerados:
     * <ul>
     *   <li>Considerar o pico do radar como limite para densidade, comparando ao valor de leituras por minuto.</li>
     *   <li>Considerar também o horário do dia (ex: 7-9 e 17-19 são horários de pico).</li>
     * </ul>
     *
     * @param readings Lista de leituras de velocidade.
     * @return Índice de tráfego calculado (1 a 5).
     */
    private Integer getTrafficIndex(List<Reading> readings) {
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

        Float averageReadingsPerMinute = readings.size() / (readingLengthMinutes == 0 ? 1 : readingLengthMinutes);
        System.out.println("Average readings per minute: " + averageReadingsPerMinute);

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
    @Override  
    public Index getCityIndex(int minutes, java.time.LocalDateTime timestamp) {
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
        Index index = getIndexFromReadings(readings);
        return index;
    }

    /**
     * Calcula uma lista de objetos {@link Index} a partir de uma lista de objetos {@link Reading}.
     * <p>
     * Este método primeiro valida a lista de leituras recebida, garantindo que não seja nula,
     * não esteja vazia e contenha pelo menos duas leituras. Em seguida, agrupa as leituras usando o
     * método {@code readingService.groupReadings} e, para cada grupo, calcula os índices de tráfego e segurança.
     * Os objetos {@link Index} resultantes são criados com os valores calculados e o intervalo de datas de cada grupo.
     * </p>
     *
     * @param readings a lista de objetos {@link Reading} a ser processada
     * @return uma lista de objetos {@link Index}, cada um representando os índices calculados para um grupo de leituras
     * @throws ResponseStatusException se a lista de leituras for nula, vazia ou contiver menos de duas leituras
     */
    @Override
    public List<Index> getIndexesWithGroupedReadings(List<Reading> readings) {
        if (readings == null || readings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Readings list is null or empty");
        }

        if (readings.size() < 2) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not enough readings to calculate index");
        }

        System.out.println("Calculating indexes from readings");
        List<ReadingInformation> groupedReadings = readingService.groupReadings(readings);
        for (ReadingInformation info : groupedReadings) {
            info.setIndex(getIndexFromReadings(info.getReadings()));
        }
        List<Index> output = groupedReadings.stream()
                .map(ReadingInformation::getIndex)
                .collect(Collectors.toList());
        System.out.println("Calculated " + output.size() + " groups from readings");

        return output;
    }

    @Override
    public Index getRadarIndexes(int minutes, String[] radars, java.time.LocalDateTime timestamp) {
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);
        System.out.println("Calculating radar index for time range: " + timeStart + " to " + timeEnd);

        List<Reading> readings = readingRepository.findByRadarIdInAndDateBetween(List.of(radars), timeStart, timeEnd);
        System.out.println("Reading count: " + readings.size());

        return getIndexFromReadings(readings);
    }

    @Override
    public Index getRegionIndex(int minutes, String region, java.time.LocalDateTime timestamp) {
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);
        System.out.println("Calculating region index for time range: " + timeStart + " to " + timeEnd);
        System.out.println(region);

        List<Reading> readings = readingRepository.findByRadarAddressRegionInAndDateBetween(List.of(region), timeStart,
                timeEnd);
        System.out.println("Reading count: " + readings.size());

        return getIndexFromReadings(readings);
    }


    @Override
    public List<RegionMap> getRegionsIndex(int minutes, java.time.LocalDateTime timestamp) {
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);
        java.time.LocalDateTime timeStartHour = timeEnd.minusMinutes(60);
        System.out.println("Calculating region index for time range: " + timeStart + " to " + timeEnd);

        List<RegionMap> regionMaps = new ArrayList<>();
        List<Region> regions = regionRepository.findAllRegions();

        for (Region region : regions) {
            RegionMap regionMap = new RegionMap("", region.getAreaRegiao(), 0, 0, 0, new HashMap<>());
            regionMap.setRegionName(region.getNomeRegiao());
            List<Reading> readings = readingRepository.findByRadarAddressRegionInAndDateBetween(
                    List.of(region.getNomeRegiao()), timeStart, timeEnd);
            if (readings.isEmpty()) {
                regionMap.setTrafficIndex(1);
                regionMap.setSecurityIndex(1);
                regionMap.setOverallIndex(1);
            }
            else {
                regionMap.setTrafficIndex(getTrafficIndex(readings));
                regionMap.setSecurityIndex(getSecurityIndex(readings));
                Integer overallIndex = Math.round((regionMap.getTrafficIndex() + regionMap.getSecurityIndex()) / 2.0f);
                regionMap.setOverallIndex(overallIndex);
            }
            List<Reading> readingsHour = readingRepository.findByRadarAddressRegionInAndDateBetween(
                    List.of(region.getNomeRegiao()), timeStartHour, timeEnd);
            for (Reading reading : readingsHour) {
                String type = reading.getVehicleType();
                regionMap.getVehicleTypeCounts().put(type, regionMap.getVehicleTypeCounts().getOrDefault(type, 0) + 1);
            }

            regionMaps.add(regionMap);
        }

        return regionMaps;
    }


    @Override
    public Index getIndexFromReadings(List<Reading> readings) {
        if (readings == null || readings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Readings list is null or empty");
        }
        Integer trafficIndex = getTrafficIndex(readings);
        Integer securityIndex = getSecurityIndex(readings);

        return new Index(securityIndex, trafficIndex, readings.get(0).getDate(),
                readings.get(readings.size() - 1).getDate());
    }

    @Override
    public List<Index> getCityIndexSeries(int minutes, LocalDateTime timestamp) {
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);
        System.out.println("Calculating city index series for time range: " + timeStart + " to " + timeEnd);
        List<Reading> readings = readingRepository.findByDateBetween(timeStart, timeEnd);
        System.out.println("Reading count: " + readings.size());
        return getIndexesWithGroupedReadings(readings);
    }

    @Override
    public List<Index> getRadarIndexesSeries(int minutes, String[] radars, LocalDateTime timestamp) {
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);
        System.out.println("Calculating radar index series for time range: " + timeStart + " to " + timeEnd);

        List<Reading> readings = readingRepository.findByRadarIdInAndDateBetween(List.of(radars), timeStart, timeEnd);
        System.out.println("Reading count: " + readings.size());

        return getIndexesWithGroupedReadings(readings);
    }

    @Override
    public Index getAddressIndexes(int minutes, String address, java.time.LocalDateTime timestamp) {
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);
        System.out.println("Calculating address index for time range: " + timeStart + " to " + timeEnd);

        List<Reading> readings = readingRepository.findByRadarAddressAddressInAndDateBetween(List.of(address), timeStart,
                timeEnd);
        System.out.println("Reading count: " + readings.size());

        Index index = getIndexFromReadings(readings);

        return index;
    }

    @Override
    public List<Index> getRegionIndexSeries(int minutes, String region, LocalDateTime timestamp) {
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);
        System.out.println("Calculating region index series for time range: " + timeStart + " to " + timeEnd);

        List<Reading> readings = readingRepository.findByRadarAddressRegionInAndDateBetween(List.of(region), timeStart,
                timeEnd);
        System.out.println("Reading count: " + readings.size());

        return getIndexesWithGroupedReadings(readings);
    }

}
