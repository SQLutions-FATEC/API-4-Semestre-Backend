package com.sqlutions.api_4_semestre_backend.repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Duration; // --- NEW ---
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.sqlutions.api_4_semestre_backend.dto.ReadingGroupAggregate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;


public class ReadingRepositoryAggregatesImpl implements ReadingRepositoryAggregates {

    @PersistenceContext
    private EntityManager entityManager;

    // Query de seleção base para selecionar dados úteis para os níveis
    private static final String QUERY_SELECT_COLUMNS = """
        , -- não deletar essa vírgula por favor.
            AVG(r.vel) AS averageSpeed,
            COUNT(r.id) AS readingCount,
            MAX(r.dat_hora) AS endTime,
            MIN(r.dat_hora) AS startTime,
            AVG(r.vel) as avgSpeed,
            MAX(r.vel) as maxSpeed,
            MIN(r.vel) as minSpeed,
            AVG(rd.vel_reg) as avgSpeedLimit,
            COUNT(r.id) FILTER (WHERE r.vel > rd.vel_reg) AS speedingCount,
            AVG(CASE WHEN r.vel > rd.vel_reg THEN r.vel - rd.vel_reg ELSE 0 END) AS averageSpeedingAmount,
            COUNT(r.id) FILTER (WHERE r.tip_vei = 'Carro')        AS carCount,
            COUNT(r.id) FILTER (WHERE r.tip_vei = 'Camionete')      AS camioneteCount,
            COUNT(r.id) FILTER (WHERE r.tip_vei = 'Onibus') AS onibusCount,
            COUNT(r.id) FILTER (WHERE r.tip_vei = 'Van')       AS vanCount,
            COUNT(r.id) FILTER (WHERE r.tip_vei = 'Caminhão grande') AS caminhãoGrandeCount,
            COUNT(r.id) FILTER (WHERE r.tip_vei = 'Moto')      AS motoCount,
            COUNT(r.id) FILTER (WHERE r.tip_vei = 'Indefinido')  AS indefinidoCount
        """;
    
    private static final String QUERY_FROM_JOINS = """
        FROM
            leitura r
        JOIN 
            radar rd ON r.id_rad = rd.id
        JOIN 
            endereco a ON rd.id_end = a.id
        """;

    @Override
    public List<ReadingGroupAggregate> findAggregatedReadings(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Optional<String> radarId,
            Optional<Integer> addressId,
            Optional<String> region
    ) {
        
        // --- 1. Criar a query dinâmica ---
        
        // --- Agrupamento de tempo adaptativo ---
        Duration duration = Duration.between(startDate, endDate);
        String timeIntervalSql;

        if (duration.toHours() < 1) {
            timeIntervalSql = "(date_trunc('hour', r.dat_hora) + floor(extract(minute from r.dat_hora) / 10) * interval '10 minute')";
        } else if (duration.toDays() <= 3) {
            timeIntervalSql = "date_trunc('hour', r.dat_hora)";
        } else {
            timeIntervalSql = "date_trunc('day', r.dat_hora)";
        }

        // --- Construir a query completa: ---
        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append(timeIntervalSql).append(" AS timeInterval "); // Agrupamento dinâmico das leituras
        sql.append(QUERY_SELECT_COLUMNS); // Valores úteis
        sql.append(QUERY_FROM_JOINS);     // Joins para selecionar por radar, endereço ou região.
        
        sql.append(" WHERE r.dat_hora BETWEEN :startDate AND :endDate ");

        // Filtros dinâmicos
        radarId.ifPresent(id -> sql.append(" AND rd.id = :radarId "));
        addressId.ifPresent(id -> sql.append(" AND a.id = :addressId "));
        region.ifPresent(r -> sql.append(" AND a.regiao = :region "));

        // Adicionar o restante da query
        // O GROUP BY funciona porque sempre aliasamos o bucket como "timeInterval"
        sql.append(" GROUP BY timeInterval ORDER BY timeInterval ASC ");

        // --- 2. Criar Query e Definir Parâmetros ---
        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        // Definir parâmetros dinâmicos
        radarId.ifPresent(id -> query.setParameter("radarId", id));
        addressId.ifPresent(id -> query.setParameter("addressId", id));
        region.ifPresent(r -> query.setParameter("region", r));

        // --- 3. Executar e mapear os resultados para o ReadingGroupAggregate ---
        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList(); 

        return mapResults(rows);
    }

    private List<ReadingGroupAggregate> mapResults(List<Object[]> rows) {
        List<ReadingGroupAggregate> result = new ArrayList<>();

        Function<Object, BigDecimal> toBigDecimal = o -> {
            if (o == null) return null;
            if (o instanceof BigDecimal) return (BigDecimal) o;
            if (o instanceof Number) return BigDecimal.valueOf(((Number) o).doubleValue());
            return new BigDecimal(o.toString());
        };
        Function<Object, Long> toLong = o -> {
            if (o == null) return 0L;
            if (o instanceof Number) return ((Number) o).longValue();
            try {
                return Long.parseLong(o.toString());
            } catch (Exception e) {
                return 0L;
            }
        };
        Function<Object, LocalDateTime> toLocalDateTime = o -> {
            if (o == null) return null;
            if (o instanceof Timestamp) return ((Timestamp) o).toLocalDateTime();
            if (o instanceof java.sql.Date) return new Timestamp(((java.sql.Date) o).getTime()).toLocalDateTime();
            return null;
        };

        for (Object[] row : rows) {
            LocalDateTime timeInterval = toLocalDateTime.apply(row[0]);
            BigDecimal averageSpeed = toBigDecimal.apply(row[1]);
            Long readingCount = toLong.apply(row[2]);
            LocalDateTime endTime = toLocalDateTime.apply(row[3]);
            LocalDateTime startTime = toLocalDateTime.apply(row[4]);
            BigDecimal avgSpeed = toBigDecimal.apply(row[5]);
            BigDecimal maxSpeed = toBigDecimal.apply(row[6]);
            BigDecimal minSpeed = toBigDecimal.apply(row[7]);
            BigDecimal avgSpeedLimit = toBigDecimal.apply(row[8]);
            Long speedingCount = toLong.apply(row[9]);
            BigDecimal averageSpeedingAmount = toBigDecimal.apply(row[10]);
            Long carCount = toLong.apply(row[11]);
            Long camioneteCount = toLong.apply(row[12]);
            Long onibusCount = toLong.apply(row[13]);
            Long vanCount = toLong.apply(row[14]);
            Long caminhaoGrandeCount = toLong.apply(row[15]);
            Long motoCount = toLong.apply(row[16]);
            Long indefinidoCount = toLong.apply(row[17]);

            result.add(new ReadingGroupAggregate(
                    timeInterval,
                    averageSpeed,
                    readingCount,
                    endTime,
                    startTime,
                    avgSpeed,
                    maxSpeed,
                    minSpeed,
                    avgSpeedLimit,
                    speedingCount,
                    averageSpeedingAmount,
                    carCount,
                    camioneteCount,
                    onibusCount,
                    vanCount,
                    caminhaoGrandeCount,
                    motoCount,
                    indefinidoCount));
        }
        return result;
    }
}