package com.currency_app.currency_app_backend.repository;

import com.currency_app.currency_app_backend.entity.ExchangeRateData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRateData, Long> {

}
