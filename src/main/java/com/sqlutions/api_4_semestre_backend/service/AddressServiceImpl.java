package com.sqlutions.api_4_semestre_backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sqlutions.api_4_semestre_backend.dto.AddressHeatMap;
import com.sqlutions.api_4_semestre_backend.dto.ReadingGroupAggregate;
import com.sqlutions.api_4_semestre_backend.entity.Address;
import com.sqlutions.api_4_semestre_backend.entity.Index;
import com.sqlutions.api_4_semestre_backend.projections.AddressGeoData;
import com.sqlutions.api_4_semestre_backend.repository.AddressRepository;
import com.sqlutions.api_4_semestre_backend.repository.ReadingRepositoryAggregates;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ReadingRepositoryAggregates readingRepository;

    @Autowired
    private IndexServiceImpl indexService;

    @Override
    public Address saveAddress(Address address) {
        return addressRepository.save(address);
    }

    @Override
    public List<Address> listAddress() {
        return addressRepository.findAll();
    }

    @Override
    public Address searchAddressById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado."));
    }

    @Override
    public Address updateAddress(Long id, Address updateAddress) {
        Address address = searchAddressById(id);
        address.setAddress(updateAddress.getAddress());
        return addressRepository.save(address);
    }

    @Override
    public void deleteAddress(Long id) {
        searchAddressById(id); // Verifica se o endereço existe
        addressRepository.deleteById(id);
    }

    @Override
    public List<AddressHeatMap> listAddressHeatMapData(int minutes, java.time.LocalDateTime timestamp) {
        java.time.LocalDateTime timeEnd = timestamp;
        java.time.LocalDateTime timeStart = timeEnd.minusMinutes(minutes);

        List<AddressHeatMap> addressesHeatMap = new ArrayList<>();
        List<AddressGeoData> geoDataList = addressRepository.ListAll();

        for (AddressGeoData geoData : geoDataList) {
            AddressHeatMap addressHeatMap = new AddressHeatMap("", "", 0, 0, 0);
            addressHeatMap.setNomeEndereco(geoData.getNomeEndereco());

            ReadingGroupAggregate aggregate = readingRepository.findSingleAggregatedReading(
                    timeStart, timeEnd, Optional.empty(), Optional.of(List.of(geoData.getNomeEndereco())),
                    Optional.empty());

            Index index;

            if (aggregate == null || aggregate.getTotalReadings() == 0) {
                index = new Index(1, 1, 1, null, null); // Retorna índices padrão se não houver dados

            } else {
                index = indexService.getIndexFromAggregate(aggregate);
            }

            addressHeatMap.setSecurityIndex(index.getSecurityIndex());
            addressHeatMap.setTrafficIndex(index.getTrafficIndex());
            addressHeatMap.setOverallIndex(index.getCombinedIndex());
            addressHeatMap.setAreaRuaGeoJson(geoData.getAreaRuaGeoJson());

            addressesHeatMap.add(addressHeatMap);

        }

        return addressesHeatMap;
    }
}
