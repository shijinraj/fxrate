package com.benz.mercedes.fxrate.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;

import com.benz.mercedes.fxrate.domain.jaxb.ExchangeRateDetails;
import com.benz.mercedes.fxrate.service.ExchangeRateService;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@AutoConfigureWebTestClient(timeout = "36000")
@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = {ExchangeRateController.class})
@Import({ExchangeRateService.class})
public class ExchangeRateControllerTest {

  @Autowired
  private WebTestClient webClient;

  @MockBean
  private ExchangeRateService exchangeRateService;

  @Test
  @DisplayName("Test Trigger Scheduler")
  void testTriggerScheduler() {
    //Given
    Mockito.when(exchangeRateService.triggerScheduler())
        .thenReturn(Mono.empty());
    //When & Then
    webClient.get().uri("/api/exchangerate")
        .exchange()
        .expectStatus().isOk();

    Mockito.verify(exchangeRateService, times(1)).triggerScheduler();
  }

  @Test
  @DisplayName("Test Get ExchangeRate with valid values")
  void testGetExchangeRate() {

    //Given
    String baseCurrency = "EUR";
    String targetCurrency = "USD";

    Mockito.when(exchangeRateService.getExchangeRate(baseCurrency, targetCurrency))
        .thenReturn(Mono.just(0.9818));
    //When & Then
    webClient.get()
        .uri("/api/exchangerate/pair/{baseCurrency}/{targetCurrency}", baseCurrency, targetCurrency)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Double.class);
    Mockito.verify(exchangeRateService, times(1)).getExchangeRate(baseCurrency, targetCurrency);
  }

  @Test
  @DisplayName("Test Exchange Rate with valid base and target currency in small cases")
  void testExchangeRateSuccessScenario() {
    //Given
    String baseCurrency = "eur";
    String targetCurrency = "usd";

    Mockito.when(exchangeRateService.getExchangeRate(baseCurrency, targetCurrency))
        .thenReturn(Mono.just(0.9818));
    //When & Then
    webClient.get()
        .uri("/api/exchangerate/pair/{baseCurrency}/{targetCurrency}", baseCurrency, targetCurrency)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Double.class);
    Mockito.verify(exchangeRateService, times(1)).getExchangeRate(baseCurrency, targetCurrency);
  }

  @Test
  @DisplayName("Test Exchange Rate with valid base and target currency in small & capital cases")
  void testExchangeRateSuccessScenario1() {
    //Given
    String baseCurrency = "eUr";
    String targetCurrency = "uSd";
    Mockito.when(exchangeRateService.getExchangeRate(baseCurrency, targetCurrency))
        .thenReturn(Mono.just(0.9818));
    //When & Then
    webClient.get()
        .uri("/api/exchangerate/pair/{baseCurrency}/{targetCurrency}", baseCurrency, targetCurrency)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Double.class);
    Mockito.verify(exchangeRateService, times(1)).getExchangeRate(baseCurrency, targetCurrency);
  }

  @Test
  @DisplayName("Test Exchange Rate with invalid base and valid target currency")
  void testExchangeRateInvalidScenario1() {
    //Given
    String baseCurrency = "ABC";
    String targetCurrency = "USD";

    Request request = Request.create(Request.HttpMethod.GET,
        "https://v6.exchangerate-api.com/v6/dfc4b4b26dafbfc23a1dfee4/pair/ABC/USD",
        new HashMap<>(), null, new RequestTemplate());
    FeignException feignException = new FeignException.NotFound("404 Not Found", request,
        new byte[0], new HashMap<>());

    doThrow(feignException).when(exchangeRateService).getExchangeRate(baseCurrency, targetCurrency);

    //When & Then
    webClient.get()
        .uri("/api/exchangerate/pair/{baseCurrency}/{targetCurrency}", baseCurrency, targetCurrency)
        .exchange()
        .expectStatus().isNotFound();

    Mockito.verify(exchangeRateService, times(1)).getExchangeRate(baseCurrency, targetCurrency);
  }

  @Test
  @DisplayName("Test Exchange Rate with valid base and invalid target currency")
  void testExchangeRateInvalidScenario2() {
    //Given
    String baseCurrency = "EUR";
    String targetCurrency = "ABC";

    Request request = Request.create(Request.HttpMethod.GET,
        "https://v6.exchangerate-api.com/v6/dfc4b4b26dafbfc23a1dfee4/pair/EUR/ABC",
        new HashMap<>(), null, new RequestTemplate());
    FeignException feignException = new FeignException.NotFound("404 Not Found", request,
        new byte[0], new HashMap<>());

    doThrow(feignException).when(exchangeRateService).getExchangeRate(baseCurrency, targetCurrency);

    //When & Then
    webClient.get()
        .uri("/api/exchangerate/pair/{baseCurrency}/{targetCurrency}", baseCurrency, targetCurrency)
        .exchange()
        .expectStatus().isNotFound();

    Mockito.verify(exchangeRateService, times(1)).getExchangeRate(baseCurrency, targetCurrency);
  }

  @Test
  @DisplayName("Test Exchange Rate with invalid base and invalid target currency")
  void testExchangeRateInvalidScenario3() {
    //Given
    String baseCurrency = "CDE";
    String targetCurrency = "FGH";

    Request request = Request.create(Request.HttpMethod.GET,
        "https://v6.exchangerate-api.com/v6/dfc4b4b26dafbfc23a1dfee4/pair/CDE/FGH",
        new HashMap<>(), null, new RequestTemplate());
    FeignException feignException = new FeignException.NotFound("404 Not Found", request,
        new byte[0], new HashMap<>());

    doThrow(feignException).when(exchangeRateService).getExchangeRate(baseCurrency, targetCurrency);

    //When & Then
    webClient.get()
        .uri("/api/exchangerate/pair/{baseCurrency}/{targetCurrency}", baseCurrency, targetCurrency)
        .exchange()
        .expectStatus().isNotFound();

    Mockito.verify(exchangeRateService, times(1)).getExchangeRate(baseCurrency, targetCurrency);

  }

  @Test
  @DisplayName("Test Exchange Rate with empty currencies")
  void testExchangeRateInvalidScenario4() {
    //Given
    String baseCurrency = "";
    String targetCurrency = "";

    //When & Then
    webClient.get()
        .uri("/api/exchangerate/pair/{baseCurrency}/{targetCurrency}", baseCurrency, targetCurrency)
        .exchange()
        .expectStatus().isNotFound();

    Mockito.verify(exchangeRateService, times(0)).getExchangeRate(baseCurrency, targetCurrency);

  }

  @Test
  @DisplayName("Test Exchange Rate with null currencies")
  void testExchangeRateInvalidScenario5() {
    //Given
    String baseCurrency = null;
    String targetCurrency = null;
    //When & Then
    webClient.get()
        .uri("/api/exchangerate/pair/{baseCurrency}/{targetCurrency}", baseCurrency, targetCurrency)
        .exchange()
        .expectStatus().isNotFound();

    Mockito.verify(exchangeRateService, times(0)).getExchangeRate(baseCurrency, targetCurrency);

  }

  @Test
  @DisplayName("Test Read Daily Report Data")
  void testReadDailyReportData() {

    //Given
    Mockito.when(exchangeRateService.readReportData(anyString()))
        .thenReturn(Mono.just(ExchangeRateDetails.builder().build()));

    //When & Then
    webClient.get().uri("/api/exchangerate/daily-report")
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .exchange()
        .expectStatus().isOk()
        .expectBody(ExchangeRateDetails.class);

    Mockito.verify(exchangeRateService, times(1)).readReportData(anyString());
  }

  @Test
  @DisplayName("Test Read Currency Exchange Report Data")
  void testReadCurrencyExchangeReportData() {

    //Given
    Mockito.when(exchangeRateService.readReportData(anyString()))
        .thenReturn(Mono.just(ExchangeRateDetails.builder().build()));

    //When & Then
    webClient.get().uri("/api/exchangerate/report")
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .exchange()
        .expectStatus().isOk()
        .expectBody(ExchangeRateDetails.class);

    Mockito.verify(exchangeRateService, times(1)).readReportData(anyString());
  }


}
