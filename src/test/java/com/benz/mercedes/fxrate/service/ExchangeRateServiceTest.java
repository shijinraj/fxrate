package com.benz.mercedes.fxrate.service;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.benz.mercedes.fxrate.ExchangeRateApplication;
import com.benz.mercedes.fxrate.domain.ExchangeRate;
import com.benz.mercedes.fxrate.domain.jaxb.ExchangeRateDetails;
import com.benz.mercedes.fxrate.rest.client.ExchangeRateClient;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {ExchangeRateApplication.class})
public class ExchangeRateServiceTest {

  @SpyBean
  private ExchangeRateService exchangeRateService;

  @SpyBean
  private ExchangeRateClient exchangeRateClient;

  @Test
  @DisplayName("Test Get Exchange Rate with valid base and target currency")
  void testGetExchangeRate() {
    //Given
    String baseCurrency = "EUR";
    String targetCurrency = "USD";
    ExchangeRate exchangeRate = ExchangeRate.builder().baseCode(baseCurrency)
        .conversionRate(0.9818).build();
    when(exchangeRateClient.getExchangeRate(baseCurrency, targetCurrency)).thenReturn(exchangeRate);

    //When
    StepVerifier.create(exchangeRateService.getExchangeRate(baseCurrency, targetCurrency))
        .expectNext(exchangeRate.getConversionRate().doubleValue())//Then
        .verifyComplete();
  }

  @Test
  @DisplayName("Test Exchange Rate with valid base and target currency in small cases")
  void testExchangeRateSuccessScenario() {
    //Given
    String baseCurrency = "eur";
    String targetCurrency = "usd";
    ExchangeRate exchangeRate = ExchangeRate.builder().baseCode(baseCurrency)
        .conversionRate(0.9818).build();
    when(exchangeRateClient.getExchangeRate(baseCurrency, targetCurrency)).thenReturn(exchangeRate);

    //When
    StepVerifier.create(exchangeRateService.getExchangeRate(baseCurrency, targetCurrency))
        .expectNext(exchangeRate.getConversionRate().doubleValue())//Then
        .verifyComplete();
  }

  @Test
  @DisplayName("Test Exchange Rate with valid base and target currency in small & capital cases")
  void testExchangeRateSuccessScenario1() {
    //Given
    String baseCurrency = "eUr";
    String targetCurrency = "uSd";
    ExchangeRate exchangeRate = ExchangeRate.builder().baseCode(baseCurrency)
        .conversionRate(0.9818).build();
    when(exchangeRateClient.getExchangeRate(baseCurrency, targetCurrency)).thenReturn(exchangeRate);

    //When
    StepVerifier.create(exchangeRateService.getExchangeRate(baseCurrency, targetCurrency))
        .expectNext(exchangeRate.getConversionRate().doubleValue())//Then
        .verifyComplete();
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

    doThrow(feignException).when(exchangeRateClient).getExchangeRate(baseCurrency, targetCurrency);

    //When
    Assertions.assertThatThrownBy(
            () -> exchangeRateService.getExchangeRate(baseCurrency, targetCurrency))
        .isInstanceOf(FeignException.class)//Then
        .hasMessageContaining("404 Not Found");
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

    doThrow(feignException).when(exchangeRateClient).getExchangeRate(baseCurrency, targetCurrency);

    //When
    Assertions.assertThatThrownBy(
            () -> exchangeRateService.getExchangeRate(baseCurrency, targetCurrency))
        .isInstanceOf(FeignException.class)//Then
        .hasMessageContaining("404 Not Found");
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

    doThrow(feignException).when(exchangeRateClient).getExchangeRate(baseCurrency, targetCurrency);

    //When
    Assertions.assertThatThrownBy(
            () -> exchangeRateService.getExchangeRate(baseCurrency, targetCurrency))
        .isInstanceOf(FeignException.class)//Then
        .hasMessageContaining("404 Not Found");
  }

  @Test
  @DisplayName("Test Exchange Rate with empty currencies")
  void testExchangeRateInvalidScenario4() {
    //Given
    String baseCurrency = "";
    String targetCurrency = "";
    Request request = Request.create(Request.HttpMethod.GET,
        "https://v6.exchangerate-api.com/v6/dfc4b4b26dafbfc23a1dfee4/pair//",
        new HashMap<>(), null, new RequestTemplate());
    FeignException feignException = new FeignException.NotFound("404 Not Found", request,
        new byte[0], new HashMap<>());

    doThrow(feignException).when(exchangeRateClient).getExchangeRate(baseCurrency, targetCurrency);

    //When
    Assertions.assertThatThrownBy(
            () -> exchangeRateService.getExchangeRate(baseCurrency, targetCurrency))
        .isInstanceOf(FeignException.class)//Then
        .hasMessageContaining("404 Not Found");
  }

  @Test
  @DisplayName("Test Exchange Rate with null currencies")
  void testExchangeRateInvalidScenario5() {
    //Given
    String baseCurrency = null;
    String targetCurrency = null;
    Request request = Request.create(Request.HttpMethod.GET,
        "https://v6.exchangerate-api.com/v6/dfc4b4b26dafbfc23a1dfee4/pair//",
        new HashMap<>(), null, new RequestTemplate());
    FeignException feignException = new FeignException.NotFound("404 Not Found", request,
        new byte[0], new HashMap<>());

    doThrow(feignException).when(exchangeRateClient).getExchangeRate(baseCurrency, targetCurrency);

    //When
    Assertions.assertThatThrownBy(
            () -> exchangeRateService.getExchangeRate(baseCurrency, targetCurrency))
        .isInstanceOf(FeignException.class)//Then
        .hasMessageContaining("404 Not Found");
  }

  @Test
  @DisplayName("Test read Report Data")
  void testReadReportData() {
    //Given
    String FILE_PATH = "./ExchangeRateDetails.xml";
    String DAILY_REPORT_FILE_PATH = "./DailyExchangeRateDetails.xml";

    //When & Then
    StepVerifier.create(exchangeRateService.readReportData(FILE_PATH))
        .verifyComplete();
  }

}
