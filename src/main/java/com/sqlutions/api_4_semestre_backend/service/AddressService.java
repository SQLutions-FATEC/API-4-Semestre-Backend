package com.sqlutions.api_4_semestre_backend.service;

import java.util.List;

import com.sqlutions.api_4_semestre_backend.dto.AddressHeatMap;
import com.sqlutions.api_4_semestre_backend.entity.Address;

public interface AddressService {

    Address saveAddress(Address address);

    List<Address> listAddress();

    Address searchAddressById(Long id);

    Address updateAddress(Long id, Address address);

    void deleteAddress(Long id);

    List<AddressHeatMap> listAddressHeatMapData(int minutes, java.time.LocalDateTime timestamp);
}
