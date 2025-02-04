package com.currency_app.currency_app_backend.service;

import com.currency_app.currency_app_backend.entity.ExchangeRateData;
import com.currency_app.currency_app_backend.repository.ExchangeRateRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class ExchangeRatesDataService {
    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRatesDataService(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @PostConstruct
    public void onStartup() {
        initialImportExchangeRate();
    }
    public void initialImportExchangeRate() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(90);
        importExchangeRates(startDate,today);
    }

    @Scheduled(cron = "0 0 17 * * ?")
    public void scheduleImportExchangeRate(){
        LocalDate today = LocalDate.now();
        importExchangeRates(today, today);
    }

    public void importExchangeRates(LocalDate startDate, LocalDate endDate) {

        String formattedStartDate = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String formattedEndDate = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        String xmlFilePath = "https://data-api.ecb.europa.eu/service/data/EXR?" +
                "startPeriod=" + formattedStartDate +
                "&endPeriod=" + formattedEndDate +
                "&format=structurespecificdata";

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;

        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        Document document = null;
        try {
            document = builder.parse(new URL(xmlFilePath).openStream());
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }

        document.getDocumentElement().normalize();

        NodeList seriesList = document.getElementsByTagName("Series");

        for (int i = 0; i < seriesList.getLength(); i++) {
            Node seriesNode = seriesList.item(i);
            if (seriesNode.getNodeType() == Node.ELEMENT_NODE) {
                Element seriesElement = (Element) seriesNode;

                String currency = seriesElement.getAttribute("CURRENCY");
                String currencyDenom = seriesElement.getAttribute("CURRENCY_DENOM");
                String title = seriesElement.getAttribute("TITLE");
                String titleCompl = seriesElement.getAttribute("TITLE_COMPL");

                if (!"EUR".equals(currencyDenom)) {
                    continue;
                }

                NodeList obsList = seriesElement.getElementsByTagName("Obs");

                for (int j = 0; j < obsList.getLength(); j++) {
                    Element obsElement = (Element) obsList.item(j);

                    String timePeriod = obsElement.getAttribute("TIME_PERIOD");
                    String obsValueStr = obsElement.getAttribute("OBS_VALUE");

                    Double obsValue = obsValueStr.isEmpty() ? null : Double.parseDouble(obsValueStr);
                    try{
                        LocalDate timestamp = LocalDate.parse(timePeriod, DateTimeFormatter.ISO_LOCAL_DATE);
                        ExchangeRateData exchangeRateData = createExchangeRateData(currency,currencyDenom,title,titleCompl,timestamp,obsValue);
                        exchangeRateRepository.save(exchangeRateData);
                    }catch (DateTimeParseException e){
                        System.out.println("Incorrect data!" + e);
                    }
                }
            }
        }

    }

    private ExchangeRateData createExchangeRateData(String currency, String currencyDenom, String title, String titleCompl, LocalDate timestamp, Double obsValue) {
        ExchangeRateData data = new ExchangeRateData();
        data.setCurrency(currency);
        data.setCurrencyDenom(currencyDenom);
        data.setTitle(title);
        data.setTitleCompl(titleCompl);
        data.setTimestamp(timestamp);
        data.setObsValue(obsValue);
        return data;
    }


    public List<ExchangeRateData> getLatestExchangeRatesForAllCurrencies() {
        return exchangeRateRepository.findLatestEntriesForAllCurrencies();
    }

    public List<ExchangeRateData> getHistoryForCurrency(String currency) {
        PageRequest pageable = PageRequest.of(0, 30);
        return exchangeRateRepository.findByCurrencyOrderByTimestampDesc(currency, pageable);
    }

    public Double calculateCurrency(String currency, Double euro){
        ExchangeRateData currencyData = exchangeRateRepository.findTopByCurrencyOrderByTimestampDesc(currency);

        if (currencyData == null) {
            throw new IllegalArgumentException("No exchange rate data found for currency: " + currency);
        }
        Double exchangeRate = currencyData.getObsValue();
        return euro * exchangeRate;
    }
}
