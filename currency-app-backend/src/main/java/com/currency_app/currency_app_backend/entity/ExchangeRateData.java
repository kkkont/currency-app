package com.currency_app.currency_app_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "EXCHANGERATE_DATA")
public class ExchangeRateData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String currency;
    private String currencyDenom;
    private Double obsValue;
    private String title;
    private String titleCompl;
    private LocalDate timestamp;


}

