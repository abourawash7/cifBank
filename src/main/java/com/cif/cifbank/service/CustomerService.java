package com.cif.cifbank.service;

import com.cif.cifbank.entity.CustomerInfo;
import com.cif.cifbank.exception.CustomerNotFoundException;
import com.cif.cifbank.exception.DuplicateCustomerException;
import com.cif.cifbank.model.CustomerDtoRequest;
import com.cif.cifbank.model.DtoCustomerResponse;
import com.cif.cifbank.model.AuthenticationResult;
import com.cif.cifbank.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CustomerService {
    private final CustomerRepository repository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public DtoCustomerResponse saveCustomerData(CustomerDtoRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        String normalizedIban = request.iban().replaceAll("\\s+", "").toUpperCase();
        String normalizedAccountNumber = request.accountNumber().trim();

        if (repository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new DuplicateCustomerException("A customer with this email already exists");
        }
        if (repository.existsByAccountNumber(normalizedAccountNumber)) {
            throw new DuplicateCustomerException("A customer with this account number already exists");
        }
        if (repository.existsByIban(normalizedIban)) {
            throw new DuplicateCustomerException("A customer with this IBAN already exists");
        }

        CustomerInfo customer = dto(request);
        customer.setEmail(normalizedEmail);
        customer.setIban(normalizedIban);
        customer.setAccountNumber(normalizedAccountNumber);
        customer.setPassword(passwordEncoder.encode(request.password()));
        customer.setRole("ROLE_USER");

        CustomerInfo savedCustomer = repository.save(customer);
        return new DtoCustomerResponse("Customer registered successfully", savedCustomer.getId());
    }
    public DtoCustomerResponse update(UUID id,String tPin) throws CustomerNotFoundException {
        if (tPin == null || tPin.isBlank()) {
            throw new IllegalArgumentException("TPIN must not be empty");
        }
        CustomerInfo customer = repository.findById(id)
                .orElseThrow(() ->
                        new CustomerNotFoundException("Customer ID not found: " + id));
        customer.setTPIN(passwordEncoder.encode(tPin));
        CustomerInfo savedCustomer = repository.save(customer);

        return new DtoCustomerResponse("Customer Update  successfully", savedCustomer.getId());
    }

    public AuthenticationResult checkStatus(UUID cutID, String tpin){
        var customer = repository.findById(cutID).orElse(null);
        if (customer == null) {
            return AuthenticationResult.INVALID_TPIN;
        }
        if (customer.getFailedAttempts() >= 3) {
            return AuthenticationResult.BLOCKED;
        }
        if (!passwordEncoder.matches(tpin, customer.getTPIN())) {
            int failedAttempts = customer.getFailedAttempts() + 1;
            customer.setFailedAttempts(failedAttempts);
            repository.save(customer);
            return failedAttempts >= 3
                    ? AuthenticationResult.BLOCKED
                    : AuthenticationResult.INVALID_TPIN;
        }

        customer.setFailedAttempts(0);
        repository.save(customer);
        return AuthenticationResult.AUTHENTICATED;
    }
    public String updateTpin(UUID id,String tPin){
        String status = null;
        var customer = repository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Customer not found"
                ));
        if(passwordEncoder.matches(tPin,customer.getTPIN())){
            customer.setTPIN(passwordEncoder.encode(tPin));
            customer.setFailedAttempts(0);
            repository.save(customer);
            status = "Tpin Update Sucess";
        }
        return status;
    }


    private CustomerInfo dto(CustomerDtoRequest request) {
        CustomerInfo itm = modelMapper.map(request, CustomerInfo.class);
        return itm;
    }
}
