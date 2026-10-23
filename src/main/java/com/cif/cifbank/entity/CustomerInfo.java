package com.cif.cifbank.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(
        name = "customer_info",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_customer_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_customer_account_number", columnNames = "account_Number"),
                @UniqueConstraint(name = "uk_customer_iban", columnNames = "iban")
        }
)
@Data
@NoArgsConstructor
public class CustomerInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "f_name")
    private String firstName;
    @Column(name = "l_name")
    private String lName;
    @Column(name = "iban", nullable = false)
    private String iban;
    @Column(name = "account_Number", nullable = false)
    private String accountNumber;
    @Column(name = "phoneNumber")
    private String phoneNumber;
    @Column(name = "BOF")
    private Date berthofDate;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "role")
    private String role;
    @Column(name = "TPIN")
    private String TPIN;

    @Column(name = "failedAttempts" )
    private Integer failedAttempts=0;

}
