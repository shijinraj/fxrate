package com.benz.mercedes.fxrate;

import static org.mockito.Mockito.times;

import com.benz.mercedes.fxrate.domain.jaxb.ExchangeRateDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ExchangeRateApplicationTests {

  @Autowired
  private WebTestClient webClient;

  @Test
  @DisplayName("Test Trigger Scheduler")
  void testTriggerScheduler() {
    webClient.get().uri("/api/exchangerate")
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  @DisplayName("Test Get ExchangeRate with valid values")
  void testGetExchangeRate() {

    //Given
    String baseCurrency = "EUR";
    String targetCurrency = "USD";

    //When & Then
    webClient.get()
        .uri("/api/exchangerate/pair/{baseCurrency}/{targetCurrency}", baseCurrency, targetCurrency)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Double.class);
  }

  @Test
  @DisplayName("Test Exchange Rate with valid base and target currency in small cases")
  void testExchangeRateSuccessScenario() {
    //Given
    String baseCurrency = "eur";
    String targetCurrency = "usd";

    //When & Then
    webClient.get()
        .uri("/api/exchangerate/pair/{baseCurrency}/{targetCurrency}", baseCurrency, targetCurrency)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Double.class);
  }

  @Test
  @DisplayName("Test Exchange Rate with valid base and target currency in small & capital cases")
  void testExchangeRateSuccessScenario1() {
    //Given
    String baseCurrency = "eUr";
    String targetCurrency = "uSd";
    //When & Then
    webClient.get()
        .uri("/api/exchangerate/pair/{baseCurrency}/{targetCurrency}", baseCurrency, targetCurrency)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Double.class);
  }

  @Test
  @DisplayName("Test Exchange Rate with invalid base and valid target currency")
  void testExchangeRateInvalidScenario1() {
    //Given
    String baseCurrency = "ABC";
    String targetCurrency = "USD";

    //When & Then
    webClient.get()
        .uri("/api/exchangerate/pair/{baseCurrency}/{targetCurrency}", baseCurrency, targetCurrency)
        .exchange()
        .expectStatus().isNotFound();

  }

  @Test
  @DisplayName("Test Exchange Rate with valid base and invalid target currency")
  void testExchangeRateInvalidScenario2() {
    //Given
    String baseCurrency = "EUR";
    String targetCurrency = "ABC";

    //When & Then
    webClient.get()
        .uri("/api/exchangerate/pair/{baseCurrency}/{targetCurrency}", baseCurrency, targetCurrency)
        .exchange()
        .expectStatus().isNotFound();

  }

  @Test
  @DisplayName("Test Exchange Rate with invalid base and invalid target currency")
  void testExchangeRateInvalidScenario3() {
    //Given
    String baseCurrency = "CDE";
    String targetCurrency = "FGH";

    //When & Then
    webClient.get()
        .uri("/api/exchangerate/pair/{baseCurrency}/{targetCurrency}", baseCurrency, targetCurrency)
        .exchange()
        .expectStatus().isNotFound();

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
  }

  @Test
  @DisplayName("Test Read Daily Report Data")
  void testReadReportData() {
    //When & Then
    webClient.get().uri("/api/exchangerate/daily-report")
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .exchange()
        .expectStatus().isOk()
        .expectBody(ExchangeRateDetails.class);
  }

  @Test
  @DisplayName("Test Read Currency Exchange Report Data")
  void testReadCurrencyExchangeReportData() {
    //When & Then
    webClient.get().uri("/api/exchangerate/report")
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .exchange()
        .expectStatus().isOk()
        .expectBody(ExchangeRateDetails.class);
  }
}
