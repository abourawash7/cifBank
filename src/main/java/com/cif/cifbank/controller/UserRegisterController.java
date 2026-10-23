package com.cif.cifbank.controller;

import com.cif.cifbank.exception.CustomerNotFoundException;
import com.cif.cifbank.model.CustomerDtoRequest;
import com.cif.cifbank.model.DtoCustomerResponse;
import com.cif.cifbank.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserRegisterController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<DtoCustomerResponse> save(@Valid @RequestBody
                                                    CustomerDtoRequest request){
        log.info("Register request received");
        DtoCustomerResponse response = customerService.saveCustomerData(request);
        log.info("Register response: status=created, customerId={}", response.uuid());
        return ResponseEntity
                .created(URI.create("/user/" + response.uuid()))
                .body(response);
    }

    @PutMapping("/{id}/{tpin}")
    private ResponseEntity<DtoCustomerResponse> update(@PathVariable("id")UUID id,@PathVariable("tpin") String tpin) throws CustomerNotFoundException {
        log.info("Set TPIN request: customerId={}, tpin=[REDACTED]", id);
        DtoCustomerResponse response = customerService.update(id,tpin);
        log.info("Set TPIN response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
