package com.sqlutions.api_4_semestre_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sqlutions.api_4_semestre_backend.service.ReadingService;

@RestController
@RequestMapping("/reading")
public class ReadingController {
    
    @Autowired
    private ReadingService readingService;

    @GetMapping("/types")
    public List<Object[]> getReadingTypes() {
        // essa função deve retornar uma série de dados para um gráfico de pizza
        // representando a porcentagem de cada tipo de veículo nas leituras
        // exemplo de retorno: ["carro: 12", "moto: 30", "caminhão: 20"]
        // para isso, você pode usar a função group by do SQL
        // SELECT tip_vei, COUNT(*) FROM leitura GROUP BY tip_vei;
        // e depois mapear o resultado para o formato desejado

        // retorno (que eu me lembre): {["carro", "moto", "caminhão"], [1, 2, 3], [[12, 13, 14], [30, 40, 50], [20, 25, 30]]}
        List<Object[]> results = readingService.getReadingTypes();
        return results;
    }

}
