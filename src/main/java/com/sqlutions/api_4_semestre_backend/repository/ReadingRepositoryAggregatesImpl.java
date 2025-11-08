package com.sqlutions.api_4_semestre_backend.repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
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

    private void buildWhereClause(StringBuilder sql,
            Optional<List<String>> radarIds,
            Optional<List<String>> addressNames,
            Optional<List<String>> regions) {

        radarIds.ifPresent(ids -> {
            if (!ids.isEmpty())
                sql.append(" AND rd.id IN (:radarIds) ");
        });
        addressNames.ifPresent(names -> {
            if (!names.isEmpty())
                sql.append(" AND a.ende IN (:addressNames) ");
        });
        regions.ifPresent(regs -> {
            if (!regs.isEmpty())
                sql.append(" AND a.regiao IN (:regions) ");
        });
    }

    /**
     * Helper para setar parâmetros para a cláusula WHERE dinâmica
     */
    private void setWhereParameters(Query query,
            Optional<List<String>> radarIds,
            Optional<List<String>> addressNames,
            Optional<List<String>> regions) {

        radarIds.ifPresent(ids -> {
            if (!ids.isEmpty())
                query.setParameter("radarIds", ids);
        });
        addressNames.ifPresent(names -> { 
            if (!names.isEmpty())
                query.setParameter("addressNames", names);
        });
        regions.ifPresent(regs -> {
            if (!regs.isEmpty())
                query.setParameter("regions", regs);
        });
    }

    @Override
    public List<ReadingGroupAggregate> findAggregatedReadingSeries(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Optional<List<String>> radarIds,
            Optional<List<String>> addressNames,
            Optional<List<String>> regions) {

        // --- 1. Criar a query dinâmica ---
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
        sql.append(timeIntervalSql).append(" AS timeInterval ");
        sql.append(QUERY_SELECT_COLUMNS);
        sql.append(QUERY_FROM_JOINS);

        sql.append(" WHERE r.dat_hora BETWEEN :startDate AND :endDate ");
        buildWhereClause(sql, radarIds, addressNames, regions);

        sql.append(" GROUP BY timeInterval ORDER BY timeInterval ASC ");

        // --- 2. Criar Query e Definir Parâmetros ---
        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        setWhereParameters(query, radarIds, addressNames, regions);

        // --- 3. Executar e mapear os resultados ---
        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();
        return mapResults(rows);
    }

    @Override
    public ReadingGroupAggregate findSingleAggregatedReading(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Optional<List<String>> radarIds,
            Optional<List<String>> addressNames,
            Optional<List<String>> regions) {
                System.out.println(regions);
        // --- 1. Construir a query completa: ---
        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append(" NULL AS timeInterval ");
        sql.append(QUERY_SELECT_COLUMNS);
        sql.append(QUERY_FROM_JOINS);

        sql.append(" WHERE r.dat_hora BETWEEN :startDate AND :endDate ");
        buildWhereClause(sql, radarIds, addressNames, regions);

        // --- 2. Criar Query e Definir Parâmetros ---
        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        setWhereParameters(query, radarIds, addressNames, regions);

        // --- 3. Executar e mapear os resultados ---
        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        if (rows.isEmpty() || ((Number) rows.get(0)[2]).intValue() == 0) {
            return null; // Nenhuma leitura encontrada
        }

        // mapResults retorna uma lista, mas queremos apenas o primeiro (e único) item
        return mapResults(rows).get(0);
    }

    private List<ReadingGroupAggregate> mapResults(List<Object[]> rows) {
        List<ReadingGroupAggregate> result = new ArrayList<>();

        Function<Object, BigDecimal> toBigDecimal = o -> {
            if (o == null)
                return null;
            if (o instanceof BigDecimal)
                return (BigDecimal) o;
            if (o instanceof Number)
                return BigDecimal.valueOf(((Number) o).doubleValue());
            return new BigDecimal(o.toString());
        };
        Function<Object, Integer> toInteger = o -> {
            if (o == null)
                return 0;
            if (o instanceof Number)
                return ((Number) o).intValue();
            try {
                return Integer.parseInt(o.toString());
            } catch (Exception e) {
                return 0;
            }
        };
        Function<Object, LocalDateTime> toLocalDateTime = o -> {
            if (o == null)
                return null;
            if (o instanceof Timestamp)
                return ((Timestamp) o).toLocalDateTime();
            if (o instanceof java.sql.Date)
                return new Timestamp(((java.sql.Date) o).getTime()).toLocalDateTime();
            return null;
        };

        for (Object[] row : rows) {
            LocalDateTime timeInterval = toLocalDateTime.apply(row[0]);
            BigDecimal averageSpeed = toBigDecimal.apply(row[1]);
            Integer readingCount = toInteger.apply(row[2]);
            LocalDateTime endTime = toLocalDateTime.apply(row[3]);
            LocalDateTime startTime = toLocalDateTime.apply(row[4]);
            BigDecimal avgSpeed = toBigDecimal.apply(row[5]);
            BigDecimal maxSpeed = toBigDecimal.apply(row[6]);
            BigDecimal minSpeed = toBigDecimal.apply(row[7]);
            BigDecimal avgSpeedLimit = toBigDecimal.apply(row[8]);
            Integer speedingCount = toInteger.apply(row[9]);
            BigDecimal averageSpeedingAmount = toBigDecimal.apply(row[10]);
            Integer carCount = toInteger.apply(row[11]);
            Integer camioneteCount = toInteger.apply(row[12]);
            Integer onibusCount = toInteger.apply(row[13]);
            Integer vanCount = toInteger.apply(row[14]);
            Integer caminhaoGrandeCount = toInteger.apply(row[15]);
            Integer motoCount = toInteger.apply(row[16]);
            Integer indefinidoCount = toInteger.apply(row[17]);

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