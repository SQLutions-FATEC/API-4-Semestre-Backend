package com.sqlutions.api_4_semestre_backend.service;

import java.time.LocalDateTime; // Importação necessária
import java.util.List;

import com.sqlutions.api_4_semestre_backend.entity.Index;
// import com.sqlutions.api_4_semestre_backend.entity.Reading; // Removido (obsoleto)
import com.sqlutions.api_4_semestre_backend.entity.RegionMap;

/**
 * Define o contrato de serviço para o cálculo de índices de segurança e
 * tráfego.
 * <p>
 * Esta interface abstrai os métodos para obter índices (únicos ou em série
 * temporal)
 * para diferentes escopos (cidade, região, radar, endereço). As implementações
 * devem calcular esses índices de forma eficiente, delegando agregações pesadas
 * para o banco de dados.
 * </p>
 * <p>
 * Os métodos que recebiam List<Reading> (getIndexFromReadings,
 * getIndexesWithGroupedReadings)
 * foram removidos por serem obsoletos após a refatoração para queries
 * agregadas.
 * </p>
 * * @author Gabriel Vasconcelos
 * 
 * @see IndexServiceImpl
 */
public interface IndexService {

    /**
     * Calcula o índice agregado (único) para toda a cidade.
     * 
     * @param minutes   Quantos minutos para trás considerar.
     * @param timestamp O "agora" do cálculo.
     * @return Um único objeto Index representando o período todo.
     */
    Index getCityIndex(int minutes, LocalDateTime timestamp);

    /**
     * Calcula o índice agregado (único) para um ou mais radares.
     * 
     * @param minutes   Quantos minutos para trás considerar.
     * @param radars    Um array de IDs de radar para filtrar.
     * @param timestamp O "agora" do cálculo.
     * @return Um único objeto Index representando o período todo.
     */
    Index getRadarIndexes(int minutes, String[] radars, LocalDateTime timestamp);

    /**
     * Calcula o índice agregado (único) para uma região.
     * 
     * @param minutes   Quantos minutos para trás considerar.
     * @param region    O nome da região para filtrar.
     * @param timestamp O "agora" do cálculo.
     * @return Um único objeto Index representando o período todo.
     */
    Index getRegionIndex(int minutes, String region, LocalDateTime timestamp);

    /**
     * Retorna os índices agregados (únicos) para todas as regiões,
     * incluindo metadados do mapa.
     * 
     * @param minutes   Quantos minutos para trás considerar.
     * @param timestamp O "agora" do cálculo.
     * @return Uma lista de RegionMap, cada um com seus índices.
     */
    List<RegionMap> getRegionsIndex(int minutes, LocalDateTime timestamp);

    /**
     * Calcula o índice agregado (único) para um endereço.
     * 
     * @param minutes   Quantos minutos para trás considerar.
     * @param address   O nome do endereço (ex: "Av. Paulista") para filtrar.
     * @param timestamp O "agora" do cálculo.
     * @return Um único objeto Index representando o período todo.
     */
    Index getAddressIndexes(int minutes, String address, LocalDateTime timestamp);

    // --- MÉTODOS DE SÉRIE TEMPORAL ---

    /**
     * Calcula uma série temporal de índices para a cidade.
     * 
     * @param minutes   Quantos minutos para trás considerar.
     * @param timestamp O "agora" do cálculo.
     * @return Uma lista de objetos Index, um para cada intervalo de tempo.
     */
    List<Index> getCityIndexSeries(int minutes, LocalDateTime timestamp);

    /**
     * Calcula uma série temporal de índices para um ou mais radares.
     * 
     * @param minutes   Quantos minutos para trás considerar.
     * @param radars    Um array de IDs de radar para filtrar.
     * @param timestamp O "agora" do cálculo.
     * @return Uma lista de objetos Index, um para cada intervalo de tempo.
     */
    List<Index> getRadarIndexesSeries(int minutes, String[] radars, LocalDateTime timestamp);

    /**
     * Calcula uma série temporal de índices para uma região.
     * 
     * @param minutes   Quantos minutos para trás considerar.
     * @param region    O nome da região para filtrar.
     * @param timestamp O "agora" do cálculo.
     * @return Uma lista de objetos Index, um para cada intervalo de tempo.
     */
    List<Index> getRegionIndexSeries(int minutes, String region, LocalDateTime timestamp);

    // --- MÉTODOS OBSOLETOS REMOVIDOS ---
    // Index getIndexFromReadings(List<Reading> readings);
    // List<Index> getIndexesWithGroupedReadings(List<Reading> readings);
}