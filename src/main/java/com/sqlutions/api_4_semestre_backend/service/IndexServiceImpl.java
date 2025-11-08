package com.sqlutions.api_4_semestre_backend.service;

import java.time.Duration; // Importado
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional; // Importado
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

// --- DTO Novo ---
import com.sqlutions.api_4_semestre_backend.dto.ReadingGroupAggregate;
import com.sqlutions.api_4_semestre_backend.entity.Index;
import com.sqlutions.api_4_semestre_backend.entity.Reading;
// --- Obsoletos (Removidos) ---
// import com.sqlutions.api_4_semestre_backend.entity.Reading;
// import com.sqlutions.api_4_semestre_backend.entity.ReadingInformation;
import com.sqlutions.api_4_semestre_backend.entity.Region;
import com.sqlutions.api_4_semestre_backend.entity.RegionMap;
import com.sqlutions.api_4_semestre_backend.repository.ReadingRepository;
import com.sqlutions.api_4_semestre_backend.repository.RegionRepository;

/**
 * Implementação do serviço responsável pelo cálculo dos índices de segurança e tráfego
 * com base nas leituras de velocidade dos radares.
 * * <p>
 * Esta classe foi refatorada para usar queries agregadas de alta performance (via ReadingRepositoryAggregates)
 * que executam os cálculos pesados diretamente no banco de dados, evitando o processamento
 * em memória de grandes listas de leituras e prevenindo OutOfMemoryErrors.
 * </p>
 * * <p>
 * Principais funcionalidades:
 * <ul>
 * <li>Cálculo de índices de segurança e tráfego a partir de dados pré-agregados (ReadingGroupAggregate).</li>
 * <li>Geração de índices únicos (para um período todo) e séries temporais (agrupados por tempo)
 * para cidade, regiões, endereços ou radares específicos.</li>
 * </ul>
 * </p>
 * * @author Gabriel Vasconcelos
 * @see IndexService
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private ReadingRepository readingRepository; // Agora contém os métodos find...Aggregated...

    @Autowired
    private RegionRepository regionRepository;

    // --- ReadingService não é mais necessário e foi removido ---
    // @Autowired
    // private ReadingService readingService;

    /**
     * Calcula o índice de segurança a partir de dados pré-agregados.
     * * @param aggregate O DTO contendo os dados agregados do banco.
     * @return Índice de segurança calculado (1 a 5).
     */
    private Integer getSecurityIndex(ReadingGroupAggregate aggregate) {
        Integer totalReadings = aggregate.getReadingCount();
        if (totalReadings == 0) {
            return 1; // Melhor índice se não há dados
        }

        Integer nOverLimit = aggregate.getSpeedingCount();
        Float overLimitWeight = 0.4f; // peso da porcentagem de carros acima do limite
        Float averageExcessWeight = 0.6f; // peso da média de excesso de velocidade
        Float percentageOverLimit = (nOverLimit * 100f) / totalReadings;
        
        // Usamos a média de excesso já calculada pelo SQL
        Float averageExcess = aggregate.getAverageSpeedingAmount().floatValue();

        Float rawIndex = ((percentageOverLimit * overLimitWeight) + (averageExcess * averageExcessWeight))
                / (overLimitWeight + averageExcessWeight);
        Integer index = Math.round(1 + 4 * (rawIndex / 100)); // mapear para 1-5
        return index;
    }
    
    /**
     * Calcula o índice de tráfego a partir de dados pré-agregados.
     *
     * @param aggregate O DTO contendo os dados agregados do banco.
     * @return Índice de tráfego calculado (1 a 5).
     */
    private Integer getTrafficIndex(ReadingGroupAggregate aggregate) {
        LocalDateTime minDate = aggregate.getStartTime();
        LocalDateTime maxDate = aggregate.getEndTime();

        // Se não há dados ou período, retorna o melhor índice
        if (minDate == null || maxDate == null || aggregate.getReadingCount() == 0) {
            return 1;
        }

        Duration readingLength = Duration.between(minDate, maxDate);
        // Evita divisão por zero se o período for muito curto
        float readingLengthMinutes = (readingLength.toSeconds() / 60.0f);
        if (readingLengthMinutes == 0) {
            readingLengthMinutes = 1; // Assume pelo menos 1 minuto para evitar divisão por zero
        }

        Float averageReadingsPerMinute = aggregate.getReadingCount() / readingLengthMinutes;

        // Usa as médias já calculadas pelo SQL
        Float averageSpeed = aggregate.getAvgSpeed().floatValue();
        Float averageRegulatedSpeed = aggregate.getAvgSpeedLimit().floatValue();

        // Evita divisão por zero se a vel_reg for 0
        if (averageRegulatedSpeed == 0) {
            return 1;
        }

        Float relativeSpeed = (averageSpeed / averageRegulatedSpeed) * 100;

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
     * Método helper principal para converter um Aggregate em um Index.
     */
    private Index getIndexFromAggregate(ReadingGroupAggregate aggregate) {
        if (aggregate == null || aggregate.getReadingCount() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma leitura encontrada para este período ou filtro.");
        }
        Integer trafficIndex = getTrafficIndex(aggregate);
        Integer securityIndex = getSecurityIndex(aggregate);

        // O timeInterval pode ser nulo (para queries "single"), 
        // então usamos startTime e endTime do aggregate.
        return new Index(securityIndex, trafficIndex, aggregate.getStartTime(), aggregate.getEndTime());
    }

    @Override  
    public Index getCityIndex(int minutes, java.time.LocalDateTime timestamp) {
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);
        System.out.println("Calculando índice da cidade (agregado) para: " + timeStart + " até " + timeEnd);

        ReadingGroupAggregate aggregate = readingRepository.findSingleAggregatedReading(
                timeStart, timeEnd, Optional.empty(), Optional.empty(), Optional.empty()
        );
        
        return getIndexFromAggregate(aggregate);
    }

    @Override
    public List<Index> getCityIndexSeries(int minutes, LocalDateTime timestamp) {
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);
        System.out.println("Calculando série de índices da cidade para: " + timeStart + " até " + timeEnd);

        List<ReadingGroupAggregate> aggregates = readingRepository.findAggregatedReadingSeries(
                timeStart, timeEnd, Optional.empty(), Optional.empty(), Optional.empty()
        );
        
        return aggregates.stream()
                .map(this::getIndexFromAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public Index getRadarIndexes(int minutes, String[] radars, java.time.LocalDateTime timestamp) {
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);
        System.out.println("Calculando índice de radares (agregado) para: " + timeStart + " até " + timeEnd);

        Optional<List<String>> radarIds = (radars != null && radars.length > 0) 
                ? Optional.of(List.of(radars)) 
                : Optional.empty();

        ReadingGroupAggregate aggregate = readingRepository.findSingleAggregatedReading(
                timeStart, timeEnd, radarIds, Optional.empty(), Optional.empty()
        );

        return getIndexFromAggregate(aggregate);
    }

    @Override
    public List<Index> getRadarIndexesSeries(int minutes, String[] radars, LocalDateTime timestamp) {
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);
        System.out.println("Calculando série de índices de radares para: " + timeStart + " até " + timeEnd);

        Optional<List<String>> radarIds = (radars != null && radars.length > 0) 
                ? Optional.of(List.of(radars)) 
                : Optional.empty();

        List<ReadingGroupAggregate> aggregates = readingRepository.findAggregatedReadingSeries(
                timeStart, timeEnd, radarIds, Optional.empty(), Optional.empty()
        );
        
        return aggregates.stream()
                .map(this::getIndexFromAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public Index getRegionIndex(int minutes, String region, java.time.LocalDateTime timestamp) {
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);
        System.out.println("Calculando índice da região (agregado) para: " + region);

        ReadingGroupAggregate aggregate = readingRepository.findSingleAggregatedReading(
                timeStart, timeEnd, Optional.empty(), Optional.empty(), Optional.of(List.of(region))
        );

        return getIndexFromAggregate(aggregate);
    }

    @Override
    public List<Index> getRegionIndexSeries(int minutes, String region, LocalDateTime timestamp) {
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);
        System.out.println("Calculando série de índices da região para: " + region);

        List<ReadingGroupAggregate> aggregates = readingRepository.findAggregatedReadingSeries(
                timeStart, timeEnd, Optional.empty(), Optional.empty(), Optional.of(List.of(region))
        );
        
        return aggregates.stream()
                .map(this::getIndexFromAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public Index getAddressIndexes(int minutes, String address, java.time.LocalDateTime timestamp) {
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);
        System.out.println("Calculando índice do endereço (agregado) para: " + address);

        ReadingGroupAggregate aggregate = readingRepository.findSingleAggregatedReading(
                timeStart, timeEnd, Optional.empty(), Optional.of(List.of(address)), Optional.empty()
        );

        return getIndexFromAggregate(aggregate);
    }

    @Override
    public List<RegionMap> getRegionsIndex(int minutes, java.time.LocalDateTime timestamp) {
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);
        System.out.println("Calculando índices de todas as regiões para: " + timeStart + " até " + timeEnd);

        List<RegionMap> regionMaps = new ArrayList<>();
        List<Region> regions = regionRepository.findAllRegions();

        for (Region region : regions) {
            RegionMap regionMap = new RegionMap("", region.getAreaRegiao(), 0, 0, 0, new HashMap<>());
            regionMap.setRegionName(region.getNomeRegiao());

            ReadingGroupAggregate aggregate = readingRepository.findSingleAggregatedReading(
                    timeStart, timeEnd, Optional.empty(), Optional.empty(), Optional.of(List.of(region.getNomeRegiao()))
            );
            
            if (aggregate == null || aggregate.getReadingCount() == 0) {
                // Se não houver dados, define os índices como os melhores (1)
                regionMap.setTrafficIndex(1);
                regionMap.setSecurityIndex(1);
                regionMap.setOverallIndex(1);
            } else {
                // Se houver dados, calcula os índices
                regionMap.setTrafficIndex(getTrafficIndex(aggregate));
                regionMap.setSecurityIndex(getSecurityIndex(aggregate));
                Integer overallIndex = Math.round((regionMap.getTrafficIndex() + regionMap.getSecurityIndex()) / 2.0f);
                regionMap.setOverallIndex(overallIndex);

                regionMap.getVehicleTypeCounts().put("Carro", aggregate.getCarCount());
                regionMap.getVehicleTypeCounts().put("Camionete", aggregate.getCamioneteCount());
                regionMap.getVehicleTypeCounts().put("Onibus", aggregate.getOnibusCount());
                regionMap.getVehicleTypeCounts().put("Van", aggregate.getVanCount());
                regionMap.getVehicleTypeCounts().put("Caminhão grande", aggregate.getCaminhaoGrandeCount());
                regionMap.getVehicleTypeCounts().put("Moto", aggregate.getMotoCount());
                regionMap.getVehicleTypeCounts().put("Indefinido", aggregate.getIndefinidoCount());
            }

            regionMaps.add(regionMap);
        }

        return regionMaps;
    }


    // --- MÉTODOS OBSOLETOS ---
    // (Não recebem mais List<Reading>, foram substituídos pelas versões "Series" e "Single")

    public List<Index> getIndexesWithGroupedReadings(List<Reading> readings) {
        // Este método que dependia de ReadingService e ReadingInformation é obsoleto.
        throw new UnsupportedOperationException("Método obsoleto. Use os métodos 'Series' (ex: getCityIndexSeries) que buscam dados agregados.");
    }

    public Index getIndexFromReadings(List<Reading> readings) {
        // Este método que processava List<Reading> em memória é obsoleto.
        throw new UnsupportedOperationException("Método obsoleto. Use os métodos 'Single' (ex: getCityIndex) que buscam dados agregados.");
    }

}