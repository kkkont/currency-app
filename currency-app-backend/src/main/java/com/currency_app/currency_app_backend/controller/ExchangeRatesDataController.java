package com.currency_app.currency_app_backend.controller;

import com.currency_app.currency_app_backend.entity.ExchangeRateData;
import com.currency_app.currency_app_backend.service.ExchangeRatesDataService;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/currency-app")
public class ExchangeRatesDataController {
    private final ExchangeRatesDataService exchangeRatesDataService;
    private static final Logger logger = getLogger(ExchangeRatesDataController.class);

    public ExchangeRatesDataController(ExchangeRatesDataService exchangeRatesDataService) {
        this.exchangeRatesDataService = exchangeRatesDataService;
    }

    /**
     * Endpoint to fetch the latest exchange rate for all currencies.
     * @return a list of currencies along with their exchange rate relative to EUR
     */
    @GetMapping("/exchange-rates/latest")
    public ResponseEntity<?> getLatestExchangeRates(){
        logger.info("Fetching latest exchange rates");
        List<ExchangeRateData> latestRates = exchangeRatesDataService.getLatestExchangeRatesForAllCurrencies();

        if (latestRates.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No exchange rates available at the moment.");
        }

        logger.info("Latest rates fetched successfully");
        return ResponseEntity.ok(latestRates);
    }

    /**
     * Endpoint to retrieve the last 30 historical entries of a specific currency.
     * @param currency – the currency for which we want to retrieve the historical data
     * @return a list of the currency's entries along with their exchange rates relative to EUR
     */
    @GetMapping("/exchange-rates/history")
    public ResponseEntity<?> getCurrencyHistory(@RequestParam String currency){
        logger.info("Fetching currency history for: " + currency);
        if (currency == null || currency.isEmpty()) {
            return ResponseEntity.badRequest().body("Currency must not be empty.");
        }

        List<ExchangeRateData> currencyHistory = exchangeRatesDataService.getHistoryForCurrency(currency);

        if (currencyHistory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No history data available for this currency.");
        }

        logger.info("Currency history fetched successfully");
        return ResponseEntity.ok(currencyHistory);
    }

    /**
     * Endpoint to calculate the equivalent sum in a different currency from a given amount in euros.
     * @param currency – the target currency to convert to
     * @param euro – the amount in euros to be converted
     * @return the equivalent sum in the target currency
     */
    @GetMapping("/exchange-rates/currencycalc")
    public ResponseEntity<?> getCalculateCurrency(@RequestParam String currency,@RequestParam Double euro){
        if (currency == null || currency.isEmpty()) {
            return ResponseEntity.badRequest().body("Currency must not be empty.");
        }

        if (euro < 0) {
            return ResponseEntity.badRequest().body("Amount must be greater than zero.");
        }
        Double sum = exchangeRatesDataService.calculateCurrency(currency,euro);
        return ResponseEntity.ok(sum);
    }
}
