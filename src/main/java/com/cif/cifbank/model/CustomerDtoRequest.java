package com.cif.cifbank.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Date;

public record CustomerDtoRequest(
        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must not exceed 50 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must not exceed 50 characters")
        String lName,

        @NotBlank(message = "IBAN is required")
        @Pattern(
                regexp = "[A-Z]{2}[0-9]{2}[A-Z0-9]{11,30}",
                message = "IBAN must contain a valid country/check prefix and 15 to 34 characters"
        )
        String iban,

        @NotBlank(message = "Account number is required")
        @Size(max = 34, message = "Account number must not exceed 34 characters")
        String accountNumber,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "\\+?[1-9][0-9]{7,14}", message = "Phone number must be a valid international number")
        String phoneNumber,

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        Date berthofDate,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 254, message = "Email must not exceed 254 characters")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 72, message = "Password must contain between 8 and 72 characters")
        String password
) {
}

