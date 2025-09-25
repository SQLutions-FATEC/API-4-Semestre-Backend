package com.sqlutions.api_4_semestre_backend.service;

import com.sqlutions.api_4_semestre_backend.entity.Adress;
import java.util.List;

public interface AdressService {

    Adress saveAdress(Adress adress);

    List<Adress> listAdress();

    Adress searchAdressById(Long id);

    Adress updateAdress(Long id, Adress adress);

    void deleteAdress(Long id);
}
