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

    /**
     * Method that is called after the service is initialized (via @PostConstruct).
     * It performs an initial import of exchange rates for the last 90 days.
     */
    @PostConstruct
    public void onStartup() {
        initialImportExchangeRate();
    }

    /**
     * Imports exchange rate data for the last 90 days from today.
     */
    public void initialImportExchangeRate() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(90);
        importExchangeRates(startDate,today);
    }

    /**
     * Schedules a daily import of exchange rates, running every day at 17:00. (as European Central Bank updates their data around 16:00)
     */
    @Scheduled(cron = "0 0 17 * * ?")
    public void scheduleImportExchangeRate(){
        LocalDate today = LocalDate.now();
        importExchangeRates(today, today); // Import exchange rates for the current day only
    }

    /**
     * Imports exchange rate data for a specified date range.
     *
     * @param startDate the start date for the import
     * @param endDate the end date for the import
     */
    public void importExchangeRates(LocalDate startDate, LocalDate endDate) {
        String formattedStartDate = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String formattedEndDate = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String xmlFilePath = buildXmlFilePath(formattedStartDate, formattedEndDate);

        try {
            Document document = parseXml(xmlFilePath);
            processExchangeRates(document);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException("Error while parsing XML data", e);
        }
    }

    /**
     * Builds the URL for the XML data based on the provided start and end dates.
     *
     * @param startDate the formatted start date for the URL
     * @param endDate the formatted end date for the URL
     * @return the constructed XML file path URL
     */
    private String buildXmlFilePath(String startDate, String endDate) {
        return "https://data-api.ecb.europa.eu/service/data/EXR?" +
                "startPeriod=" + startDate +
                "&endPeriod=" + endDate +
                "&format=structurespecificdata";
    }

    /**
     * Parses the XML data from the given URL.
     *
     * @param xmlFilePath the URL to the XML file containing exchange rate data
     * @return the parsed XML document
     * @throws ParserConfigurationException if a configuration error occurs during parsing
     * @throws SAXException if an XML parsing error occurs
     * @throws IOException if there is an issue with the network or file reading
     */
    private Document parseXml(String xmlFilePath) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new URL(xmlFilePath).openStream());
    }

    /**
     * Processes the exchange rates from the XML document and saves the valid data to the repository.
     *
     * @param document the parsed XML document containing exchange rate data
     */
    private void processExchangeRates(Document document) {
        document.getDocumentElement().normalize();
        NodeList seriesList = document.getElementsByTagName("Series");

        for (int i = 0; i < seriesList.getLength(); i++) {
            Node seriesNode = seriesList.item(i);
            if (seriesNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element seriesElement = (Element) seriesNode;
            String currencyDenom = seriesElement.getAttribute("CURRENCY_DENOM");

            if (!"EUR".equals(currencyDenom)) {
                continue; // Skip currencies not denominated in EUR
            }

            processObservations(seriesElement);
        }
    }

    /**
     * Processes each observation in the given series element and saves the valid ones to the repository.
     *
     * @param seriesElement the XML element representing a currency series
     */
    private void processObservations(Element seriesElement) {
        NodeList obsList = seriesElement.getElementsByTagName("Obs");

        for (int j = 0; j < obsList.getLength(); j++) {
            Element obsElement = (Element) obsList.item(j);
            String timePeriod = obsElement.getAttribute("TIME_PERIOD");
            String obsValueStr = obsElement.getAttribute("OBS_VALUE");

            Double obsValue = parseObsValue(obsValueStr);
            LocalDate timestamp = parseTimestamp(timePeriod);

            if (timestamp != null) {
                ExchangeRateData exchangeRateData = createExchangeRateData(
                        seriesElement.getAttribute("CURRENCY"),
                        seriesElement.getAttribute("CURRENCY_DENOM"),
                        seriesElement.getAttribute("TITLE"),
                        seriesElement.getAttribute("TITLE_COMPL"),
                        timestamp,
                        obsValue
                );
                exchangeRateRepository.save(exchangeRateData);
            }
        }
    }

    /**
     * Parses the observation value (OBS_VALUE) as a Double.
     *
     * @param obsValueStr the string representation of the observation value
     * @return the parsed Double value, or null if the string is empty
     */
    private Double parseObsValue(String obsValueStr) {
        return obsValueStr.isEmpty() ? null : Double.parseDouble(obsValueStr);
    }

    /**
     * Parses the time period (TIME_PERIOD) as a LocalDate.
     *
     * @param timePeriod the string representation of the time period
     * @return the parsed LocalDate, or null if the parsing fails
     */
    private LocalDate parseTimestamp(String timePeriod) {
        try {
            return LocalDate.parse(timePeriod, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format: " + timePeriod + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Creates an ExchangeRateData object using the provided values.
     *
     * @param currency the currency code (e.g., USD)
     * @param currencyDenom the currency denomination (should be EUR)
     * @param title the title of the exchange rate
     * @param titleCompl the additional title details
     * @param timestamp the date of the exchange rate
     * @param obsValue the exchange rate value
     * @return a new ExchangeRateData object with the given values
     */
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


    /**
     * Retrieves the latest exchange rates for all currencies.
     *
     * @return a list of the latest exchange rate data for all currencies
     */
    public List<ExchangeRateData> getLatestExchangeRatesForAllCurrencies() {
        return exchangeRateRepository.findLatestEntriesForAllCurrencies();
    }


    /**
     * Retrieves the historical exchange rates for a given currency.
     *
     * @param currency the currency code to retrieve historical data for
     * @return a list of the most recent exchange rate data for the given currency
     */
    public List<ExchangeRateData> getHistoryForCurrency(String currency) {
        PageRequest pageable = PageRequest.of(0, 30);
        return exchangeRateRepository.findByCurrencyOrderByTimestampDesc(currency, pageable);
    }


    /**
     * Calculates the equivalent value of a given amount of EUR in another currency.
     *
     * @param currency the currency code to convert to
     * @param euro the amount in EUR to convert
     * @return the equivalent value in the target currency
     * @throws IllegalArgumentException if no exchange rate data is found for the given currency
     */
    public Double calculateCurrency(String currency, Double euro){
        ExchangeRateData currencyData = exchangeRateRepository.findTopByCurrencyOrderByTimestampDesc(currency);

        if (currencyData == null) {
            throw new IllegalArgumentException("No exchange rate data found for currency: " + currency);
        }
        Double exchangeRate = currencyData.getObsValue();
        return euro * exchangeRate;
    }
}
