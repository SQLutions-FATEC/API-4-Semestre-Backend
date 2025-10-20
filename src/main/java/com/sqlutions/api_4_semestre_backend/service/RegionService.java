package com.sqlutions.api_4_semestre_backend.service;

import com.sqlutions.api_4_semestre_backend.entity.Address;

import java.util.List;

import com.sqlutions.api_4_semestre_backend.entity.Region;

public interface RegionService {

    Address saveAddress(Address address);

    List<Address> listAddress();

    Region searchAddressById(Long id);

    Address updateAddress(Long id, Address address);

    void deleteAddress(Long id);
}
