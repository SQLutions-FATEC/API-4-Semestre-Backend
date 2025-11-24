package com.sqlutions.api_4_semestre_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sqlutions.api_4_semestre_backend.dto.ReadingGroupAggregate;
import com.sqlutions.api_4_semestre_backend.entity.Radar;
import com.sqlutions.api_4_semestre_backend.entity.Reading;
import com.sqlutions.api_4_semestre_backend.entity.ReadingInformation;
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

    // OBSOLETO
    @Override
    @Deprecated
    public List<ReadingInformation> getReadingsFromLastMinutes(int minutes,
            @Nullable java.time.LocalDateTime startDate) {

        LocalDateTime endDate = startDate != null ? startDate : timeService.getCurrentTimeClampedToDatabase();
        startDate = endDate.minusMinutes(minutes);
        System.out.println("Fetching readings from " + startDate + " to " + endDate);

        List<Reading> readings = readingRepository.findByDateBetween(startDate, endDate);
        return groupReadings(readings);
    }

    // OBSOLETO
    @Override
    @Deprecated
    public List<ReadingInformation> getReadingsFromLastMinutesByAddress(List<String> address, int minutes,
            @Nullable java.time.LocalDateTime startDate) {
        List<Reading> readings = readingRepository.findByRadarAddressAddressInAndDateBetween(address,
                (startDate != null ? startDate : timeService.getCurrentTimeClampedToDatabase()).minusMinutes(minutes),
                startDate != null ? startDate : timeService.getCurrentTimeClampedToDatabase());
        return groupReadings(readings);
    }

    // OBSOLETO
    @Override
    @Deprecated
    public List<ReadingInformation> getReadingsFromLastMinutesByRadar(List<String> radarIds, int minutes,
            @Nullable java.time.LocalDateTime startDate) {
        List<Reading> readings = readingRepository.findByRadarIdInAndDateBetween(radarIds,
                (startDate != null ? startDate : timeService.getCurrentTimeClampedToDatabase()).minusMinutes(minutes),
                startDate != null ? startDate : timeService.getCurrentTimeClampedToDatabase());
        return groupReadings(readings);
    }

    // OBSOLETO
    @Override
    @Deprecated
    public List<ReadingInformation> getReadingsFromLastMinutesByAddressRegion(List<String> regions, int minutes,
            @Nullable java.time.LocalDateTime startDate) {
        List<Reading> readings = readingRepository.findByRadarAddressRegionInAndDateBetween(regions,
                (startDate != null ? startDate : timeService.getCurrentTimeClampedToDatabase()).minusMinutes(minutes),
                startDate != null ? startDate : timeService.getCurrentTimeClampedToDatabase());
        return groupReadings(readings);
    }

    /**
     * Agrupa uma lista de {@link Reading} em "grupos" (intervalos) baseados no tempo, de acordo com o período amostral,
     * e retorna-os como objetos {@link ReadingInformation}.
     * <p>
     * A estratégia de agrupamento é determinada pela duração entre a leitura mais antiga e a mais recente:
     * <ul>
     * <li>Se o período for menor que 1 hora, as leituras são agrupadas em intervalos de 10 minutos.</li>
     * <li>Se o período for menor que 1 dia, as leituras são agrupadas por hora.</li>
     * <li>Se o período for maior que 3 dias, as leituras são agrupadas por dia.</li>
     * </ul>
     * @deprecated Use {@link ReadingRepositoryAggregates} para consultas agregadas diretamente no banco de dados.
     * @param readings a lista de {@link Reading} a ser agrupada; não deve ser nula e deve conter pelo menos um elemento
     * @return uma lista de {@link ReadingInformation}, cada item contendo informações agregadas para um intervalo de tempo
     */
    // OBSOLETO
    @Override
    @Deprecated
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
        } else if (samplePeriod.compareTo(java.time.Duration.ofDays(3)) < 0) {
            System.out.println("Sample period is less than 3 days, grouping by hour");
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

        /**
         * Recupera uma leitura agregada (um único objeto {@link ReadingGroupAggregate}) para um intervalo de tempo
         * determinado pelos últimos {@code minutes} minutos até o momento corrente do sistema.
         *
         * O método:
         * - Obtém o instante final ("endDate") usando {@code timeService.getCurrentTimeClampedToDatabase()}.
         * - Calcula o instante inicial ("startDate") subtraindo {@code minutes} do instante final.
         * - Invoca {@code readingRepository.findSingleAggregatedReading(startDate, endDate, ...)} passando
         *   filtros opcionais para identificadores de radares, endereços e regiões.
         *
         * Observações:
         * - Os parâmetros {@code radarIds}, {@code addresses} e {@code regionIds} são opcionais; quando nulos,
         *   não são aplicados filtros correspondentes.
         * - Espera-se que {@code minutes} seja um valor positivo representando a janela em minutos.
         *
         * @param minutes intervalo em minutos para agregação (janela de tempo até o momento corrente)
         * @param radarIds lista opcional de IDs de radar para filtrar os dados; pode ser {@code null}
         * @param addresses lista opcional de endereços para filtrar os radares; pode ser {@code null}
         * @param regionIds lista opcional de IDs de região para filtrar os radares; pode ser {@code null}
         * @return um {@link ReadingGroupAggregate} contendo as métricas agregadas no período especificado
         * @throws org.springframework.web.server.ResponseStatusException com status {@code 404 NOT_FOUND}
         *         se o repositório não retornar nenhuma agregação (resultado nulo)
         */
    @Override
    public ReadingGroupAggregate getReadings(int minutes, @Nullable LocalDateTime timestamp, @Nullable List<String> radarIds, @Nullable List<String> addresses,
            @Nullable List<String> regionIds) {
        LocalDateTime endDate = timestamp != null ? timestamp : timeService.getCurrentTimeClampedToDatabase();
        LocalDateTime startDate = endDate.minusMinutes(minutes);
        System.out.println("Fetching aggregated readings from " + startDate + " to " + endDate);
        ReadingGroupAggregate reading = readingRepository.findSingleAggregatedReading(
                startDate,
                endDate,
                Optional.ofNullable(radarIds),
                Optional.ofNullable(addresses),
                Optional.ofNullable(regionIds));
        if (reading == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Readings list is null or empty");
        }
        return reading;
    }

    /**
     * Recupera uma série temporal de leituras agregadas ({@link ReadingGroupAggregate}) para uma janela
     * de tempo composta pelos últimos {@code minutes} minutos terminando em {@code timestamp} (ou no momento
     * corrente do sistema se {@code timestamp} for {@code null}).
     *
     * O método:
     * - Determina o instante final ("endDate") usando {@code timestamp} quando fornecido, caso contrário
     *   usa {@code timeService.getCurrentTimeClampedToDatabase()}.
     * - Calcula o instante inicial ("startDate") subtraindo {@code minutes} do instante final.
     * - Invoca {@code readingRepository.findAggregatedReadingSeries(startDate, endDate, ...)} passando
     *   filtros opcionais para identificadores de radares, endereços e regiões.
     *
     * Observações:
     * - Os parâmetros {@code radarIds}, {@code addresses} e {@code regionIds} são opcionais; quando nulos,
     *   não são aplicados filtros correspondentes.
     * - {@code timestamp} pode ser {@code null}, caso em que o ponto final da janela será o tempo atual
     *   "clamped" do serviço de tempo.
     * - Espera-se que {@code minutes} seja um valor positivo que define a duração da janela.
     *
     * @param minutes duração da janela em minutos (até {@code timestamp} ou até o tempo atual quando {@code null})
     * @param timestamp instante final opcional para a série; quando {@code null} usa-se o tempo atual "clamped"
     * @param radarIds lista opcional de IDs de radar para filtrar os dados; pode ser {@code null}
     * @param addresses lista opcional de endereços para filtrar os radares; pode ser {@code null}
     * @param regionIds lista opcional de IDs de região para filtrar os radares; pode ser {@code null}
     * @return uma lista de {@link ReadingGroupAggregate} representando a série temporal agregada no período solicitado
     * @throws org.springframework.web.server.ResponseStatusException com status {@code 404 NOT_FOUND}
     *         se o repositório retornar uma lista vazia (nenhuma agregação encontrada)
     */
    @Override
    public List<ReadingGroupAggregate> getReadingSeries(int minutes, @Nullable LocalDateTime timestamp, @Nullable List<String> radarIds,
            @Nullable List<String> addresses,
            @Nullable List<String> regionIds) {
        LocalDateTime endDate = timestamp != null ? timestamp : timeService.getCurrentTimeClampedToDatabase();
        LocalDateTime startDate = endDate.minusMinutes(minutes);
        List<ReadingGroupAggregate> readings = readingRepository.findAggregatedReadingSeries(
                startDate,
                endDate,
                Optional.ofNullable(radarIds),
                Optional.ofNullable(addresses),
                Optional.ofNullable(regionIds));
        if (readings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Readings list is null or empty");
        }
        return readings;
    }
}
