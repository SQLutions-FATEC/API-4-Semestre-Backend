package com.sqlutions.api_4_semestre_backend.service;

import com.sqlutions.api_4_semestre_backend.entity.Adress;
import com.sqlutions.api_4_semestre_backend.repository.AdressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdressServiceImpl implements AdressService {

    @Autowired
    private AdressRepository adressRepository;

    @Override
    public Adress saveAdress(Adress adress) {
        return adressRepository.save(adress);
    }

    @Override
    public List<Adress> listAdress() {
        return adressRepository.findAll();
    }

    @Override
    public Adress searchAdressById(Long id) {
        return adressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado."));
    }

    @Override
    public Adress updateAdress(Long id, Adress updateAdress) {
        Adress adress = searchAdressById(id);
        adress.setAdress(updateAdress.getAdress());
        return adressRepository.save(adress);
    }

    @Override
    public void deleteAdress(Long id) {
        adressRepository.deleteById(id);
    }
}
