package com.benz.mercedes.fxrate.service;

import com.benz.mercedes.fxrate.domain.jaxb.ExchangeRateDetails;
import reactor.core.publisher.Mono;

public interface ExchangeRateService {

  /**
   * Trigger manual exchange rate fetching process - Fetch currency rates from external service
   * every two hours and compares with ExchangeRateDetails xml for the last available rate of the
   * respective currency. Then it adds a new entry if there are any difference , else not. Also
   * triggers a Daily Report Scheduler
   */
  Mono<Void> triggerScheduler();

  /**
   * Read current exchange rate in terms of X/Y. Example USD/EUR
   */
  Mono<Double> getExchangeRate(String baseCurrency, String targetCurrency);


  /**
   * Read the report data in terms of base currency: EUR
   */
  Mono<ExchangeRateDetails> readReportData(String fileLocation);

}
