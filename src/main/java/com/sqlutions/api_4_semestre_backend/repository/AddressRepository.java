package com.sqlutions.api_4_semestre_backend.repository;

import com.sqlutions.api_4_semestre_backend.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}


