package com.currency_app.currency_app_backend.controller;

import com.currency_app.currency_app_backend.entity.ExchangeRateData;
import com.currency_app.currency_app_backend.service.ExchangeRatesDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/currency-app")
public class ExchangeRatesDataController {
    private final ExchangeRatesDataService exchangeRatesDataService;

    public ExchangeRatesDataController(ExchangeRatesDataService exchangeRatesDataService) {
        this.exchangeRatesDataService = exchangeRatesDataService;
    }


    @GetMapping("/exchange-rates/latest")
    public ResponseEntity<List<ExchangeRateData>> getLatestExchangeRates(){
        List<ExchangeRateData> latestRates = exchangeRatesDataService.getLatestExchangeRatesForAllCurrencies();

        if (latestRates.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(latestRates);
    }

    @GetMapping("/exchange-rates/history")
    public ResponseEntity<List<ExchangeRateData>> getCurrencyHistory(@RequestParam String currency){
        List<ExchangeRateData> currencyHistory = exchangeRatesDataService.getHistoryForCurrency(currency);

        if (currencyHistory.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(currencyHistory);
    }

    @GetMapping("/exchange-rates/currencycalc")
    public ResponseEntity<Double> getCalculateCurrency(@RequestParam String currency,@RequestParam Double euro){
        Double sum = exchangeRatesDataService.calculateCurrency(currency,euro);
        return ResponseEntity.ok(sum);
    }
}
