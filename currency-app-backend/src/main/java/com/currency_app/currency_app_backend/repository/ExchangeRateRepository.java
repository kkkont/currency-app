package com.currency_app.currency_app_backend.repository;

import com.currency_app.currency_app_backend.entity.ExchangeRateData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRateData, Long> {

    /**
     * Finds a list of exchange rates for a given currency ordered by timestamp in descending order,
     * with pagination support.
     */
    List<ExchangeRateData> findByCurrencyOrderByTimestampDesc(String currency, Pageable pageable);

    /**
     * Finds the latest exchange rate entry for each currency, ordered by currency.
     */
    @Query("SELECT e FROM ExchangeRateData e WHERE e.timestamp = (" +
            "SELECT MAX(innerE.timestamp) FROM ExchangeRateData innerE " +
            "WHERE innerE.currency = e.currency) ORDER BY e.currency")
    List<ExchangeRateData> findLatestEntriesForAllCurrencies();

    /**
     * Finds the latest exchange rate entry for a given currency.
     */
    ExchangeRateData findTopByCurrencyOrderByTimestampDesc(String currency);

}
