package com.benz.mercedes.fxrate.rest.client;

import com.benz.mercedes.fxrate.domain.ExchangeRate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Exchange Client Test")
public class ExchangeRateClientTest {

  @Autowired
  private ExchangeRateClient exchangeRateClient;

  @Test
  @DisplayName("Test Get Exchange Rate for a Base Currency")
  void testGetExchangeRateForBaseCurrency() {
    // Given , When
    ExchangeRate exchangeRate = exchangeRateClient.getExchangeRateForEuroBaseCurrency();

    // Then
    Assertions.assertThat(exchangeRate).isNotNull().extracting(ExchangeRate::getConversionRates)
        .isNotNull();
    Assertions.assertThat(exchangeRate).isNotNull().extracting(ExchangeRate::getConversionRate)
        .isNull();
  }


  @Test
  @DisplayName("Test Exchange Rate with valid base and target currency")
  void testExchangeRate() {
    // Given , When
    ExchangeRate exchangeRate = exchangeRateClient.getExchangeRate("EUR", "USD");

    // Then
    Assertions.assertThat(exchangeRate).isNotNull().extracting(ExchangeRate::getConversionRates)
        .isNull();
    Assertions.assertThat(exchangeRate).isNotNull().extracting(ExchangeRate::getConversionRate)
        .isNotNull();
  }

  @Test
  @DisplayName("Test Exchange Rate with valid base and target currency in small cases")
  void testExchangeRateSuccessScenario() {
    // Given , When
    ExchangeRate exchangeRate = exchangeRateClient.getExchangeRate("eur", "usd");

    // Then
    Assertions.assertThat(exchangeRate).isNotNull().extracting(ExchangeRate::getConversionRates)
        .isNull();
    Assertions.assertThat(exchangeRate).isNotNull().extracting(ExchangeRate::getConversionRate)
        .isNotNull();
  }

  @Test
  @DisplayName("Test Exchange Rate with valid base and target currency in small & capital cases")
  void testExchangeRateSuccessScenario1() {
    // Given , When
    ExchangeRate exchangeRate = exchangeRateClient.getExchangeRate("eUr", "uSd");

    // Then
    Assertions.assertThat(exchangeRate).isNotNull().extracting(ExchangeRate::getConversionRates)
        .isNull();
    Assertions.assertThat(exchangeRate).isNotNull().extracting(ExchangeRate::getConversionRate)
        .isNotNull();
  }

  @Test
  @DisplayName("Test Exchange Rate with invalid base and valid target currency")
  void testExchangeRateInvalidScenario1() {
    // Given , When and Then
    Assertions.assertThatThrownBy(() -> exchangeRateClient.getExchangeRate("ABC", "USD"))
        .hasMessageContaining("404 Not Found")
        .hasMessageContaining("unsupported-code");
  }

  @Test
  @DisplayName("Test Exchange Rate with valid base and invalid target currency")
  void testExchangeRateInvalidScenario2() {
    // Given , When and Then
    Assertions.assertThatThrownBy(() -> exchangeRateClient.getExchangeRate("EUR", "ABC"))
        .hasMessageContaining("404 Not Found")
        .hasMessageContaining("unsupported-code");
  }

  @Test
  @DisplayName("Test Exchange Rate with invalid base and invalid target currency")
  void testExchangeRateInvalidScenario3() {
    // Given , When and Then
    Assertions.assertThatThrownBy(() -> exchangeRateClient.getExchangeRate("CDE", "ABC"))
        .hasMessageContaining("404 Not Found")
        .hasMessageContaining("unsupported-code");
  }

  @Test
  @DisplayName("Test Exchange Rate with empty currencies")
  void testExchangeRateInvalidScenario4() {
    // Given , When and Then
    Assertions.assertThatThrownBy(() -> exchangeRateClient.getExchangeRate("", ""))
        .hasMessageContaining("404 Not Found");
  }

  @Test
  @DisplayName("Test Exchange Rate with null currencies")
  void testExchangeRateInvalidScenario5() {
    // Given , When and Then
    Assertions.assertThatThrownBy(() -> exchangeRateClient.getExchangeRate(null, null))
        .hasMessageContaining("404 Not Found");
  }

}
