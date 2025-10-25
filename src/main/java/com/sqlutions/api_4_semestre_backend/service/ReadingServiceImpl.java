package com.sqlutions.api_4_semestre_backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sqlutions.api_4_semestre_backend.entity.HourlyVehicleData;
import com.sqlutions.api_4_semestre_backend.entity.ReadingInformation;
import com.sqlutions.api_4_semestre_backend.entity.Radar;
import com.sqlutions.api_4_semestre_backend.entity.Reading;
import com.sqlutions.api_4_semestre_backend.entity.VehicleTypeHourlyData;
import com.sqlutions.api_4_semestre_backend.repository.RadarRepository;
import com.sqlutions.api_4_semestre_backend.repository.ReadingRepository;

import io.micrometer.common.lang.Nullable;

/**
 * Serviço responsável pelo gerenciamento de leituras (Readings) de radares.
 * 
 * Esta implementação fornece métodos para buscar, agrupar, criar, atualizar e
 * deletar leituras,
 * além de realizar consultas filtradas por endereço, radar, região e bairro,
 * considerando períodos de tempo.
 * 
 * Funcionalidades principais:
 * <ul>
 * <li>Buscar leituras dos últimos minutos, com ou sem filtros por endereço,
 * radar, região ou bairro.</li>
 * <li>Agrupar leituras em intervalos de tempo dinâmicos (10 minutos, hora ou
 * dia) conforme o período analisado.</li>
 * <li>Criar, atualizar e remover leituras, validando a existência do radar
 * associado.</li>
 * <li>Obter todas as leituras agrupadas.</li>
 * <li>Buscar leitura individual pelo identificador.</li>
 * </ul>
 * 
 * Observações:
 * <ul>
 * <li>O agrupamento das leituras é feito conforme a duração do período
 * analisado, otimizando a visualização dos dados.</li>
 * <li>O serviço depende dos repositórios de leitura e radar, além de um serviço
 * de tempo para manipulação de datas.</li>
 * <li>Em caso de inconsistências ou dados não encontrados, são lançadas
 * exceções apropriadas com status HTTP.</li>
 * </ul>
 * 
 * @author Gabriel Vasconcelos
 * @see ReadingService
 * @see ReadingRepository
 * @see RadarRepository
 * @see TimeService
 */
@Service
public class ReadingServiceImpl implements ReadingService {
    @Autowired
    private ReadingRepository readingRepository;

    @Autowired
    private RadarRepository radarRepository;

    @Autowired
    private TimeService timeService;

    @Override
    public List<ReadingInformation> getReadingsFromLastMinutes(int minutes,
            @Nullable java.time.LocalDateTime startDate) {

        LocalDateTime endDate = startDate != null ? startDate : timeService.getCurrentTimeClampedToDatabase();
        startDate = endDate.minusMinutes(minutes);
        System.out.println("Fetching readings from " + startDate + " to " + endDate);

        List<Reading> readings = readingRepository.findByDateBetween(startDate, endDate);
        return groupReadings(readings);
    }

    @Override
    public List<ReadingInformation> getReadingsFromLastMinutesByAddress(List<String> address, int minutes,
            @Nullable java.time.LocalDateTime startDate) {
        List<Reading> readings = readingRepository.findByRadarAddressAddressInAndDateBetween(address,
                (startDate != null ? startDate : timeService.getCurrentTimeClampedToDatabase()).minusMinutes(minutes),
                startDate != null ? startDate : timeService.getCurrentTimeClampedToDatabase());
        return groupReadings(readings);
    }

    @Override
    public List<ReadingInformation> getReadingsFromLastMinutesByRadar(List<String> radarIds, int minutes,
            @Nullable java.time.LocalDateTime startDate) {
        List<Reading> readings = readingRepository.findByRadarIdInAndDateBetween(radarIds,
                (startDate != null ? startDate : timeService.getCurrentTimeClampedToDatabase()).minusMinutes(minutes),
                startDate != null ? startDate : timeService.getCurrentTimeClampedToDatabase());
        return groupReadings(readings);
    }

    @Override
    public List<ReadingInformation> getReadingsFromLastMinutesByAddressRegion(List<String> regions, int minutes,
            @Nullable java.time.LocalDateTime startDate) {
        List<Reading> readings = readingRepository.findByRadarAddressRegionInAndDateBetween(regions,
                (startDate != null ? startDate : timeService.getCurrentTimeClampedToDatabase()).minusMinutes(minutes),
                startDate != null ? startDate : timeService.getCurrentTimeClampedToDatabase());
        return groupReadings(readings);
    }

    /**
     * Groups a list of {@link Reading} objects into time-based buckets depending on
     * the sample period and returns them as {@link ReadingInformation} objects.
     * <p>
     * The grouping strategy is determined by the duration between the earliest and
     * latest reading:
     * <ul>
     * <li>If the period is less than 1 hour, readings are grouped into 10-minute
     * intervals.</li>
     * <li>If the period is less than 1 day, readings are grouped by hour.</li>
     * <li>If the period is more than 3 days, readings are grouped by day.</li>
     * </ul>
     *
     * @param readings the list of {@link Reading} objects to group; must not be
     *                 null and should contain at least one element
     * @return a list of {@link ReadingInformation} objects, each containing
     *         aggregated information for a time bucket
     */
    @Override
    public List<ReadingInformation> groupReadings(List<Reading> readings) {
        if (readings == null || readings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Readings list is null or empty");
        }
        java.time.LocalDateTime firstReadingDate = readings.stream()
                .map(Reading::getDate)
                .min(java.time.LocalDateTime::compareTo)
                .orElse(null);
        java.time.LocalDateTime lastReadingDate = readings.stream()
                .map(Reading::getDate)
                .max(java.time.LocalDateTime::compareTo)
                .orElse(null);
        List<List<Reading>> groupedReadings = new java.util.ArrayList<>();
        System.out.println("First reading date: " + firstReadingDate);
        System.out.println("Last reading date: " + lastReadingDate);
        java.time.Duration samplePeriod = java.time.Duration.between(firstReadingDate, lastReadingDate);
        if (samplePeriod.compareTo(java.time.Duration.ofHours(1)) < 0) {
            System.out.println("Sample period is less than 1 hour, grouping by 10 minutes");
            readings.sort((r1, r2) -> r1.getDate().compareTo(r2.getDate()));
            groupedReadings = new java.util.ArrayList<>(
                    readings.stream()
                            .collect(java.util.stream.Collectors.groupingBy(r -> r.getDate()
                                    .withMinute((r.getDate().getMinute() / 10) * 10).withSecond(0).withNano(0)))
                            .values());
        } else if (samplePeriod.compareTo(java.time.Duration.ofDays(1)) < 0) {
            System.out.println("Sample period is less than a day, grouping by hour");
            readings.sort((r1, r2) -> r1.getDate().compareTo(r2.getDate()));
            groupedReadings = new java.util.ArrayList<>(
                    readings.stream()
                            .collect(java.util.stream.Collectors.groupingBy(r -> r.getDate().withMinute(0).withSecond(0)
                                    .withNano(0).withHour(r.getDate().getHour())))
                            .values());
        } else if (samplePeriod.compareTo(java.time.Duration.ofDays(3)) > 0) {
            System.out.println("Sample period is more than 3 days, grouping by day");
            readings.sort((r1, r2) -> r1.getDate().compareTo(r2.getDate()));
            groupedReadings = new java.util.ArrayList<>(
                    readings.stream()
                            .collect(java.util.stream.Collectors
                                    .groupingBy(r -> r.getDate().withHour(0).withMinute(0).withSecond(0).withNano(0)))
                            .values());
        }

        return groupedReadings.stream()
                .map(ReadingInformation::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadingInformation> getAllReadings() {
        return groupReadings(readingRepository.findAll());
    }

    @Override
    public Reading getReadingById(Integer id) {
        return readingRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reading with id " + id + " not found"));
    }

    @Override
    public Reading createReading(Reading reading) {
        String radarId = reading.getRadar().getId();
        Radar radar = radarRepository.findById(radarId).orElse(null);
        if (radar == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Radar with id " + radarId + " not found");
        } else {
            reading.setRadar(radar);
        }
        // TODO: validar tipos de veículo para a leitura.
        // há uma incoerência entre o ENUM e a estrutura do banco de dados.

        return readingRepository.save(reading);
    }

    @Override
    public Reading updateReading(Reading reading) {
        String radarId = reading.getRadar().getId();
        if (radarId != null) {
            Radar radar = radarRepository.findById(radarId).orElse(null);
            if (radar == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Radar with id " + radarId + " not found");
            }
            reading.setRadar(radar);
        }
        return readingRepository.save(reading);
    }

    @Override
    public Void deleteReading(Integer id) {
        readingRepository.deleteById(id);
        return null;
    }

    @Override
    public List<VehicleTypeHourlyData> getHourlyVehicleDataByType(@Nullable LocalDateTime startTime,
            @Nullable LocalDateTime endTime, @Nullable String vehicleType, @Nullable List<String> regions) {
        // Lógica para definir o período de tempo
        if (startTime == null && endTime == null) {
            // Caso padrão: últimas 24 horas
            endTime = timeService.getCurrentTimeClampedToDatabase();
            startTime = endTime.minusHours(24);
        } else if (startTime != null && endTime == null) {
            // Só startTime fornecido: período de 24 horas a partir do startTime
            endTime = startTime.plusHours(24);
        } else if (startTime == null && endTime != null) {
            // Só endTime fornecido: 24 horas antes do endTime
            startTime = endTime.minusHours(24);
        }
        // Se ambos fornecidos, usar como estão

        List<VehicleTypeHourlyData> result = new ArrayList<>();

        // Se um tipo de veículo específico foi fornecido, retornar apenas esse tipo
        if (vehicleType != null && !vehicleType.trim().isEmpty()) {
            List<Reading> readings = getReadingsForFilters(startTime, endTime, vehicleType, regions);
            result.add(createVehicleTypeHourlyData(vehicleType, readings, startTime, endTime));
            return result;
        }

        // Caso contrário, obter todos os tipos de veículos para o período e região
        // especificados
        List<String> vehicleTypes = getDistinctVehicleTypes(startTime, endTime, regions);

        // Processar dados para "Todos" os tipos
        List<Reading> allReadings = getReadingsForFilters(startTime, endTime, null, regions);
        result.add(createVehicleTypeHourlyData("Todos", allReadings, startTime, endTime));

        // Processar dados para cada tipo específico
        for (String type : vehicleTypes) {
            if (type != null && !type.trim().isEmpty()) {
                List<Reading> readingsForType = getReadingsForFilters(startTime, endTime, type, regions);
                result.add(createVehicleTypeHourlyData(type, readingsForType, startTime, endTime));
            }
        }

        return result;
    }

    private List<Reading> getReadingsForFilters(LocalDateTime startTime, LocalDateTime endTime,
            @Nullable String vehicleType, @Nullable List<String> regions) {

        // Se há filtro por região e tipo de veículo
        if (regions != null && !regions.isEmpty() && vehicleType != null && !vehicleType.trim().isEmpty()) {
            return readingRepository.findByRadarAddressRegionInAndDateBetweenAndVehicleType(
                    regions, startTime, endTime, vehicleType);
        }

        // Se há filtro apenas por região
        if (regions != null && !regions.isEmpty()) {
            if (vehicleType != null && !vehicleType.trim().isEmpty()) {
                // Filtrar por região primeiro, depois por tipo de veículo
                List<Reading> regionReadings = readingRepository.findByRadarAddressRegionInAndDateBetween(
                        regions, startTime, endTime);
                return regionReadings.stream()
                        .filter(reading -> vehicleType.equals(reading.getVehicleType()))
                        .collect(Collectors.toList());
            } else {
                return readingRepository.findByRadarAddressRegionInAndDateBetween(regions, startTime, endTime);
            }
        }

        // Se há filtro apenas por tipo de veículo
        if (vehicleType != null && !vehicleType.trim().isEmpty()) {
            return readingRepository.findByDateBetweenAndVehicleType(startTime, endTime, vehicleType);
        }

        // Sem filtros específicos
        return readingRepository.findByDateBetween(startTime, endTime);
    }

    private List<String> getDistinctVehicleTypes(LocalDateTime startTime, LocalDateTime endTime,
            @Nullable List<String> regions) {
        if (regions != null && !regions.isEmpty()) {
            return readingRepository.findDistinctVehicleTypesByRegionAndDateBetween(regions, startTime, endTime);
        }
        return readingRepository.findDistinctVehicleTypesBetweenDates(startTime, endTime);
    }

    private VehicleTypeHourlyData createVehicleTypeHourlyData(String vehicleType, List<Reading> readings,
            LocalDateTime startTime, LocalDateTime endTime) {
        // Agrupar as leituras por hora truncada
        Map<LocalDateTime, List<Reading>> hourlyReadingsMap = readings.stream()
                .collect(Collectors.groupingBy(
                        reading -> reading.getDate().withMinute(0).withSecond(0).withNano(0)));

        // Calcular número de horas no período
        long hoursInPeriod = java.time.Duration.between(startTime, endTime).toHours();

        List<HourlyVehicleData> hourlyData = new ArrayList<>();
        for (int i = 0; i < hoursInPeriod; i++) {
            LocalDateTime hour = startTime.plusHours(i).withMinute(0).withSecond(0).withNano(0);
            List<Reading> hourReadings = hourlyReadingsMap.getOrDefault(hour, new ArrayList<>());

            int count = hourReadings.size();
            double averageSpeed = hourReadings.isEmpty() ? 0.0
                    : hourReadings.stream().mapToInt(Reading::getSpeed).average().orElse(0.0);

            hourlyData.add(new HourlyVehicleData(hour, count, averageSpeed));
        }

        return new VehicleTypeHourlyData(vehicleType, hourlyData);
    }

}
