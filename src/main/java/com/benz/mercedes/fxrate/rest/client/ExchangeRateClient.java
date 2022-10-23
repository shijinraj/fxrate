package com.benz.mercedes.fxrate.rest.client;

import com.benz.mercedes.fxrate.domain.ExchangeRate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = ExchangeRateClient.SERVICE_NAME, url = "https://v6.exchangerate-api.com/v6/dfc4b4b26dafbfc23a1dfee4/")
public interface ExchangeRateClient {

  String SERVICE_NAME = "exchangeRateClient";

  @GetMapping("/latest/EUR")
  ExchangeRate getExchangeRateForEuroBaseCurrency();

  @GetMapping("/pair/{baseCurrency}/{targetCurrency}")
  ExchangeRate getExchangeRate(@PathVariable String baseCurrency,
      @PathVariable String targetCurrency);

}
