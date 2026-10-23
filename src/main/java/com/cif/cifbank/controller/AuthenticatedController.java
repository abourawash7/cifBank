package com.cif.cifbank.controller;

import com.cif.cifbank.model.ResquestDtoBody;
import com.cif.cifbank.model.AuthenticationResult;
import com.cif.cifbank.service.CustomerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("auth")
@AllArgsConstructor
@Slf4j
public class AuthenticatedController {

    private final CustomerService customerService;
    @PostMapping("{CustomerID}/{tpin}")
    public ResponseEntity<String> Verify(@PathVariable("CustomerID") UUID uuid,@PathVariable("tpin") String tpin){
        log.info("Authenticate request: customerId={}, tpin=[REDACTED]", uuid);
        AuthenticationResult result = customerService.checkStatus(uuid, tpin);
        log.info("Authenticate response: customerId={}, result={}", uuid, result);
        return switch (result) {
            case AUTHENTICATED -> ResponseEntity.ok("Authentication successful");
            case INVALID_TPIN -> ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid CIF or TPIN");
            case BLOCKED -> ResponseEntity
                    .status(HttpStatus.LOCKED)
                    .body("CIF is blocked");
        };
    }

    @PutMapping("{CustomerID}/{tpin}")
    public ResponseEntity<String> updateTipn(@RequestBody ResquestDtoBody request,
    @PathVariable("CustomerID") UUID id,@PathVariable("tpin") String tPin){
        log.info("Reset TPIN request: customerId={}, pathTpin=[REDACTED], bodyTpin=[REDACTED]", id);
        String response = customerService.updateTpin(id, tPin);
        log.info("Reset TPIN response: customerId={}, result={}", id, response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
