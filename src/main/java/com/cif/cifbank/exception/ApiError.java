package com.cif.cifbank.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiError(
        int status,
        String message,
        String path,
        LocalDateTime timestamp,
        Map<String, String> errors
) {
}
