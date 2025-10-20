package com.sqlutions.api_4_semestre_backend.service;

import java.util.List;

import com.sqlutions.api_4_semestre_backend.entity.BusStop;

public interface BusStopService {

    // Read (Todos)
    List<BusStop> listBusStops();

    // Read (Por ID)
    BusStop searchBusStopById(Long id);

    // Create / Update
    // Se o ID for null ou não existir, cria; se existir, atualiza.

    // Delete
    void deleteBusStop(Long id);

    // (Opcional, mas útil) Você pode adicionar aqui métodos espaciais
    // Por exemplo: List<PontoOnibus> findPointsInRegion(Long regiaoId);
}