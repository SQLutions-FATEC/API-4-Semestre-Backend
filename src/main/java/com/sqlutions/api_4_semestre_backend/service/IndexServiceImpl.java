package com.sqlutions.api_4_semestre_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import com.sqlutions.api_4_semestre_backend.entity.Reading;
import com.sqlutions.api_4_semestre_backend.repository.ReadingRepository;

@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private ReadingRepository readingRepository;

    @Autowired
    private TimeService timeService;
    // CREATE TABLE leitura (
    // id SERIAL PRIMARY KEY,
    // id_rad VARCHAR(9) NOT NULL, --refere-se ao id do radar
    // dat_hora TIMESTAMP NOT NULL,
    // tip_vei tipo_veiculo NOT NULL,
    // vel INT NOT NULL,
    // CONSTRAINT fk_leitura_radar FOREIGN KEY (id_rad) REFERENCES radar(id)
    // );

    @Description("Dada uma lista de leituras, calcular o índice de segurança. Retorna um número de 1 a 5. (menor é melhor)")
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

    public Integer getCityIndex() {
        // essa função vai pegar todos os outros índices, e retornar um número de 1 a 5
        // (menor é melhor).
        // lógica para calcular o índice geral da cidade:
        // pegar todos os outros índices e fazer a média

        // índice de tráfego:
        // dadas todas as leituras de tráfego do último min: ()

        // índice de segurança:
        // dadas todas as leituras de velocidade do último min, calcular a porcentagem
        // de carros que estão acima da velocidade máxima
        java.time.LocalDateTime timeEnd = timeService.getCurrentTimeClampedToDatabase();
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(600);
        System.out.println("Calculating city index for time range: " + timeStart + " to " + timeEnd);
        List<Reading> readings = readingRepository.findByDateBetween(timeStart, timeEnd);
        System.out.println("Reading count: " + readings.size());
        Integer securityIndex = getSecurityIndex(readings);

        return securityIndex;
    }

}
