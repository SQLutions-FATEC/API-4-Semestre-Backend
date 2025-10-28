package com.sqlutions.api_4_semestre_backend.service;

import com.sqlutions.api_4_semestre_backend.entity.Address;
import java.util.List;

public interface AddressService {

    Address saveAddress(Address address);

    List<Address> listAddress();

    Address searchAddressById(Long id);

    Address updateAddress(Long id, Address address);

    void deleteAddress(Long id);
}
