package com.benz.mercedes.fxrate.service;

import com.benz.mercedes.fxrate.domain.ConversionRates;
import com.benz.mercedes.fxrate.domain.ExchangeRate;
import com.benz.mercedes.fxrate.domain.jaxb.CurrencyChangeDetails;
import com.benz.mercedes.fxrate.domain.jaxb.ExchangeRateDetails;
import com.benz.mercedes.fxrate.rest.client.ExchangeRateClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

@Service
@Slf4j
public class ExchangeRateServiceImpl implements ExchangeRateService {

  private static String FILE_PATH = "./ExchangeRateDetails.xml";
  private static String DAILY_REPORT_FILE_PATH = "./DailyExchangeRateDetails.xml";

  @Autowired
  private ExchangeRateClient exchangeRateClient;


  /**
   * Trigger manual exchange rate fetching process - Fetch currency rates from external service
   * every two hours and compares with ExchangeRateDetails xml for the last available rate of the
   * respective currency. Then it adds a new entry if there are any difference , else not.
   */
  @Override
  public Mono<Void> triggerScheduler() {

    long currencyRateSchedulerIntervalInHours = 2l;//2 hour
    long dailyReportSchedulerIntervalInDays = 1l;//1 day

    executeCurrencyRateScheduler(currencyRateSchedulerIntervalInHours);

    executeDailyReportScheduler(dailyReportSchedulerIntervalInDays);

    return Mono.empty();

  }

  private void executeDailyReportScheduler(long currencyRateReportIntervalInDays) {
    Flux.interval(Duration.ofDays(currencyRateReportIntervalInDays))
        .doFirst(
            () -> log.info(
                "********************** Starting A daily report job scheduling **********************"))
        .map(aLong -> {
          ExchangeRateDetails exchangeRateDetails = null;
          try {
            exchangeRateDetails = readFile(JAXBContext.newInstance(ExchangeRateDetails.class,
                CurrencyChangeDetails.class), FILE_PATH);
          } catch (JAXBException | FileNotFoundException exception) {
            log.error("Exception while reading the file {} for the daily report job",
                exception.getMessage());
          }
          return exchangeRateDetails;
        })
        .map(exchangeRateDetails -> get24HourExchangeRateDetails(exchangeRateDetails))
        .onErrorContinue((err, i) -> log.error("An error happened for the daily report job {} {}", i,
            err.getMessage()))
        .doOnNext(exchangeRateDetails -> log.info("Daily report exchange rate - {}",
            exchangeRateDetails))
        .subscribe(this::createDailyReport);
  }

  /**
   * Fetch currency rates from external service every the respective hour interval and saves to a
   * file which contains certain exchange rates: USD, GBP, EUR, CHF
   *
   * @param currencyRateIntervalInHours
   */
  private void executeCurrencyRateScheduler(long currencyRateIntervalInHours) {
    Flux.interval(Duration.ofHours(currencyRateIntervalInHours))
        .map(aLong -> exchangeRateClient.getExchangeRateForEuroBaseCurrency())
        .doFirst(() -> log.info(
            "********************** Starting Manual Exchange Rate Fetch job scheduling **********************"))
        .doOnNext(exchangeRate -> log.info("Exchange Rate For Euro BaseCurrency - {} ",
            exchangeRate))
        .onErrorContinue(
            (err, i) -> log.error("An error happened for the Manual Exchange Rate Fetch job {} {}",
                i, err.getMessage()))
        .subscribe(this::saveExchangeRate);
  }

  private ExchangeRateDetails get24HourExchangeRateDetails(
      ExchangeRateDetails exchangeRateDetails) {
    Date date = new Date();
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.HOUR, -24);//24 Hour back time
    Date twentyFourHourBack = cal.getTime();
    log.info("24 hour back date is {} and current date {} ", twentyFourHourBack, date);
    //Compare and get the 24-hour data from the two hours' report
    Optional.of(exchangeRateDetails).map(ExchangeRateDetails::getEuroCurrencyChanges)
        .filter(
            currencyChangeDetailsList -> !CollectionUtils.isEmpty(currencyChangeDetailsList))
        .ifPresent(currencyChangeDetailsList -> currencyChangeDetailsList.removeIf(
            currencyChangeDetails ->
                currencyChangeDetails.getDate() != null
                    && (currencyChangeDetails.getDate().compareTo(twentyFourHourBack) == -1
                    // remove all dates which is older than twentyFourHourBack date
                    || currencyChangeDetails.getDate().compareTo(date)
                    == 1))); // remove all the dates which is newer than current date

    Optional.of(exchangeRateDetails).map(ExchangeRateDetails::getUsdCurrencyChanges)
        .filter(
            currencyChangeDetailsList -> !CollectionUtils.isEmpty(currencyChangeDetailsList))
        .ifPresent(currencyChangeDetailsList -> currencyChangeDetailsList.removeIf(
            currencyChangeDetails ->
                currencyChangeDetails.getDate() != null
                    && (currencyChangeDetails.getDate().compareTo(twentyFourHourBack) == -1
                    // remove all dates which is older than twentyFourHourBack date
                    || currencyChangeDetails.getDate().compareTo(date)
                    == 1))); // remove all the dates which is newer than current date

    Optional.of(exchangeRateDetails).map(ExchangeRateDetails::getGbpCurrencyChanges)
        .filter(
            currencyChangeDetailsList -> !CollectionUtils.isEmpty(currencyChangeDetailsList))
        .ifPresent(currencyChangeDetailsList -> currencyChangeDetailsList.removeIf(
            currencyChangeDetails ->
                currencyChangeDetails.getDate() != null
                    && (currencyChangeDetails.getDate().compareTo(twentyFourHourBack) == -1
                    // remove all dates which is older than twentyFourHourBack date
                    || currencyChangeDetails.getDate().compareTo(date)
                    == 1))); // remove all the dates which is newer than current date

    Optional.of(exchangeRateDetails).map(ExchangeRateDetails::getChfCurrencyChanges)
        .filter(
            currencyChangeDetailsList -> !CollectionUtils.isEmpty(currencyChangeDetailsList))
        .ifPresent(currencyChangeDetailsList -> currencyChangeDetailsList.removeIf(
            currencyChangeDetails ->
                currencyChangeDetails.getDate() != null
                    && (currencyChangeDetails.getDate().compareTo(twentyFourHourBack) == -1
                    // remove all dates which is older than twentyFourHourBack date
                    || currencyChangeDetails.getDate().compareTo(date)
                    == 1))); // remove all the dates which is newer than current date

    return exchangeRateDetails;
  }

  @Override
  public Mono<Double> getExchangeRate(String baseCurrency, String targetCurrency) {
    return Optional.of(exchangeRateClient.getExchangeRate(baseCurrency, targetCurrency))
        .map(ExchangeRate::getConversionRate).map(
            Mono::just).orElse(null);
  }

  @SneakyThrows
  @Override
  public Mono<ExchangeRateDetails> readReportData(String fileLocation) {
    return Optional.ofNullable(JAXBContext.newInstance(ExchangeRateDetails.class,
            CurrencyChangeDetails.class)).map(jaxbContext -> {
          ExchangeRateDetails exchangeRateDetails = null;
          try {
            exchangeRateDetails = readFile(jaxbContext, fileLocation);
          } catch (JAXBException | FileNotFoundException exception) {
            log.error("Exception while reading the file {} ", exception.getMessage());
          }
          if (exchangeRateDetails == null) {
            log.info("Daily Report Data is empty");
          }
          return exchangeRateDetails;
        })
        .map(Mono::just)
        .orElseGet(Mono::empty);//returns empty if no ExchangeRateDetails xml available
  }

  private void saveExchangeRate(ExchangeRate exchangeRate) {
    log.debug("saveExchangeRate starts");
    Date date = new Date();
    JAXBContext context = null;
    try {
      context = JAXBContext.newInstance(ExchangeRateDetails.class,
          CurrencyChangeDetails.class);

      ExchangeRateDetails exchangeRateDetails = readFile(context, FILE_PATH);

      if (exchangeRateDetails != null) {//ExchangeRateDetails xml exists
        updateCurrencyChanges(exchangeRate, date, exchangeRateDetails);
      } else {//ExchangeRateDetails xml did not exists
        exchangeRateDetails = getExchangeRateDetails(exchangeRate, date);
      }

      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(exchangeRateDetails, new File(FILE_PATH));
    } catch (JAXBException | FileNotFoundException exception) {
      log.error("Exception in saveExchangeRate {} ", exception.getMessage());
    }

    log.debug("saveExchangeRate completed");
  }

  private void createDailyReport(ExchangeRateDetails exchangeRateDetails) {
    log.info("Inside create daily report method");
    JAXBContext context = null;
    try {
      context = JAXBContext.newInstance(ExchangeRateDetails.class,
          CurrencyChangeDetails.class);

      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(exchangeRateDetails,
          new File(DAILY_REPORT_FILE_PATH));
    } catch (JAXBException exception) {
      log.error("Exception in createDailyReport {} ", exception.getMessage());
    }

    log.info("Inside create daily report generation completed");
  }

  private ExchangeRateDetails getExchangeRateDetails(ExchangeRate exchangeRate, Date date) {
    ExchangeRateDetails exchangeRateDetails;
    exchangeRateDetails = ExchangeRateDetails.builder().build();
    exchangeRateDetails.setEuroCurrencyChanges(Arrays.asList(
        CurrencyChangeDetails.builder().value(exchangeRate.getConversionRates().getEur())
            .date(date).build()));
    exchangeRateDetails.setUsdCurrencyChanges(Arrays.asList(
        CurrencyChangeDetails.builder().value(exchangeRate.getConversionRates().getUsd())
            .date(date).build()));
    exchangeRateDetails.setGbpCurrencyChanges(Arrays.asList(
        CurrencyChangeDetails.builder().value(exchangeRate.getConversionRates().getGbp())
            .date(date).build()));
    exchangeRateDetails.setChfCurrencyChanges(Arrays.asList(
        CurrencyChangeDetails.builder().value(exchangeRate.getConversionRates().getChf())
            .date(date).build()));
    return exchangeRateDetails;
  }

  /**
   * Compares the currency change details with the given exchange rate
   *
   * @param exchangeRate
   * @param date
   * @param exchangeRateDetails
   */
  private void updateCurrencyChanges(ExchangeRate exchangeRate, Date date,
      ExchangeRateDetails exchangeRateDetails) {
    boolean isThereAnyChangeInCurrencyRate = false;
    //Euro
    CurrencyChangeDetails euroCurrencyChangeDetails = exchangeRateDetails.getEuroCurrencyChanges()
        .get(exchangeRateDetails.getEuroCurrencyChanges().size() - 1);
    if (exchangeRate.getConversionRates().getEur() != null &&
        euroCurrencyChangeDetails.getValue().compareTo(exchangeRate.getConversionRates().getEur())
            != 0) {
      log.info("Euro Rate changed from {} to {}", euroCurrencyChangeDetails.getValue(),
          Optional.of(exchangeRate).map(ExchangeRate::getConversionRates).map(
              ConversionRates::getEur).orElse(null));
      isThereAnyChangeInCurrencyRate = true;
      exchangeRateDetails.getEuroCurrencyChanges().add(
          CurrencyChangeDetails.builder().value(exchangeRate.getConversionRates().getEur())
              .date(date).build());
    }

    //USD
    CurrencyChangeDetails usdCurrencyChangeDetails = exchangeRateDetails.getUsdCurrencyChanges()
        .get(exchangeRateDetails.getUsdCurrencyChanges().size() - 1);
    if (exchangeRate.getConversionRates().getUsd() != null &&
        usdCurrencyChangeDetails.getValue().compareTo(exchangeRate.getConversionRates().getUsd())
            != 0) {
      log.info("USD Rate changed from {} to {}", usdCurrencyChangeDetails.getValue(),
          Optional.of(exchangeRate).map(ExchangeRate::getConversionRates).map(
              ConversionRates::getUsd).orElse(null));
      isThereAnyChangeInCurrencyRate = true;
      exchangeRateDetails.getUsdCurrencyChanges().add(
          CurrencyChangeDetails.builder().value(exchangeRate.getConversionRates().getUsd())
              .date(date).build());
    }

    //GBP
    CurrencyChangeDetails gbpCurrencyChangeDetails = exchangeRateDetails.getGbpCurrencyChanges()
        .get(exchangeRateDetails.getGbpCurrencyChanges().size() - 1);
    if (exchangeRate.getConversionRates().getGbp() != null &&
        gbpCurrencyChangeDetails.getValue().compareTo(exchangeRate.getConversionRates().getGbp())
            != 0) {
      log.info("GBP Rate changed from {} to {}", gbpCurrencyChangeDetails.getValue(),
          Optional.of(exchangeRate).map(ExchangeRate::getConversionRates).map(
              ConversionRates::getGbp).orElse(null));
      isThereAnyChangeInCurrencyRate = true;
      exchangeRateDetails.getGbpCurrencyChanges().add(
          CurrencyChangeDetails.builder().value(exchangeRate.getConversionRates().getGbp())
              .date(date).build());
    }

    //CHF
    CurrencyChangeDetails chfCurrencyChangeDetails = exchangeRateDetails.getChfCurrencyChanges()
        .get(exchangeRateDetails.getChfCurrencyChanges().size() - 1);
    if (exchangeRate.getConversionRates().getChf() != null &&
        chfCurrencyChangeDetails.getValue().compareTo(exchangeRate.getConversionRates().getChf())
            != 0) {
      log.info("CHF Rate changed from {} to {}", chfCurrencyChangeDetails.getValue(),
          Optional.of(exchangeRate).map(ExchangeRate::getConversionRates).map(
              ConversionRates::getChf).orElse(null));
      exchangeRateDetails.getChfCurrencyChanges().add(
          CurrencyChangeDetails.builder().value(exchangeRate.getConversionRates().getChf())
              .date(date).build());
    }

    if (!isThereAnyChangeInCurrencyRate) {
      log.info("No change in the currency rates !!!");
    }
  }

  private ExchangeRateDetails readFile(JAXBContext context, String filePath)
      throws JAXBException, FileNotFoundException {

    ExchangeRateDetails exchangeRateDetailsFromFile = null;
    Marshaller marshaller = context.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

    File file = new File(filePath);

    log.info("ExchangeRate file exists {}", file.exists());

    if (file.exists()) {
      try (FileReader fileReader = new FileReader(filePath)) {
        exchangeRateDetailsFromFile = (ExchangeRateDetails) context.createUnmarshaller()
            .unmarshal(fileReader);
      } catch (IOException ioException) {
        log.error("Exception while reading the file {} {} ", FILE_PATH, ioException.getMessage());
      }

    }
    return exchangeRateDetailsFromFile;
  }


}
