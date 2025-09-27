package com.sqlutions.api_4_semestre_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sqlutions.api_4_semestre_backend.entity.Address;
import com.sqlutions.api_4_semestre_backend.entity.Radar;
import com.sqlutions.api_4_semestre_backend.repository.AddressRepository;
import com.sqlutions.api_4_semestre_backend.repository.RadarRepository;

@Service
public class RadarServiceImpl implements RadarService {
    @Autowired
    RadarRepository radarRepository;

    @Autowired
    AddressRepository addressRepository;

    @Override
    public List<Radar> getAllRadars() {
        return radarRepository.findAll();
    }

    @Override
    public Radar getRadarById(String id) {
        return radarRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Radar with id " + id + " not found"));
    }

    @Override
    public Radar createRadar(Radar radar) {
        Long addressId = radar.getAddress().getId();
        Address address = addressRepository.findById(addressId).orElse(null);
        if (address == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Address with id " + addressId + " not found");
        } else {
            radar.setAddress(address);
        }
        return radarRepository.save(radar);
    }

    @Override
    public Radar updateRadar(Radar radar) {
        Long addressId = radar.getAddress().getId();
        Address address = addressRepository.findById(addressId).orElse(null);
        if (address == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Address with id " + addressId + " not found");
        } else {
            radar.setAddress(address);
        }
        return radarRepository.save(radar);
    }

    @Override
    public Void deleteRadar(String id) {
        radarRepository.deleteById(id);
        return null;
    }

}
