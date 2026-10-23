package com.cif.cifbank.repository;

import com.cif.cifbank.entity.CustomerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<CustomerInfo, UUID> {
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByAccountNumber(String accountNumber);

    boolean existsByIban(String iban);


    Optional<CustomerInfo> findByEmail(String email);

    Optional<CustomerInfo> findByEmailIgnoreCase(String email);


}
