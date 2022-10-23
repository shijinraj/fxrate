package com.benz.mercedes.fxrate.controller;

import com.benz.mercedes.fxrate.domain.jaxb.ExchangeRateDetails;
import com.benz.mercedes.fxrate.exception.Error;
import com.benz.mercedes.fxrate.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Optional;


@Slf4j
@RestController
@RequestMapping(value = "/api/exchangerate")
@Tag(name = "Order Management", description = "The Order management APIs")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "OK"),
    @ApiResponse(responseCode = "201", description = "Created"),
    @ApiResponse(responseCode = "400", description = "Bad Request", content = {
        @Content(mediaType = "application/json",
            schema = @Schema(implementation = Error.class))}),
    @ApiResponse(responseCode = "415", description = "Unsupported Media Type", content = {
        @Content(mediaType = "application/json",
            schema = @Schema(implementation = Error.class))}),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
        @Content(mediaType = "application/json",
            schema = @Schema(implementation = Error.class))}),})
public class ExchangeRateController {

  @Autowired
  private ExchangeRateService exchangeRateService;

  public static String DAILY_REPORT_FILE_PATH = "./DailyExchangeRateDetails.xml";

  private static String FILE_PATH = "./ExchangeRateDetails.xml";


  @Operation(description = "Trigger Currency Exchange Rate Scheduler and Daily Report Scheduler",
      summary = "Trigger Currency Exchange Rate Scheduler and Daily Report Scheduler", tags = {
      "TriggerCurrency Exchange Rate Scheduler and Daily Report Scheduler"})
  @GetMapping
  public Mono<ResponseEntity<HttpStatus>> triggerScheduler() {
    log.info("Started Trigger Currency Exchange Rate Scheduler and Daily Report Schedulers");
    return exchangeRateService.triggerScheduler()
        .thenReturn(ResponseEntity.status(HttpStatus.OK).build());
  }

  @Operation(description = "Read current exchange rate in terms of X/Y. Example USD/EUR",
      summary = "Read current exchange rate in terms of X/Y. Example USD/EUR", tags = {
      "Read current exchange rate in terms of X/Y. Example USD/EUR"})
  @GetMapping("/pair/{baseCurrency}/{targetCurrency}")
  public Mono<ResponseEntity<Double>> getExchangeRate(@PathVariable final String baseCurrency,
      final @PathVariable String targetCurrency) {
    log.debug("Started Read current exchange rate in terms of baseCurrency {} targetCurrency {} ",
        baseCurrency, targetCurrency);
    return exchangeRateService.getExchangeRate(baseCurrency, targetCurrency)
        .map(ResponseEntity.status(HttpStatus.OK)::body);
  }

  @Operation(description = "Reads the daily report data in terms of base currency: EUR",
      summary = "Reads the daily report data in terms of base currency: EUR", tags = {
      "Reads the daily report data in terms of base currency: EUR"})
  @GetMapping(value = "/daily-report")
  public Mono<ResponseEntity<ExchangeRateDetails>> readDailyReportData() {
    log.debug("Started Read the report data ");
    return exchangeRateService.readReportData(DAILY_REPORT_FILE_PATH).map(ResponseEntity.status(HttpStatus.OK)::body);
  }

  @Operation(description = "Reads the Currency Exchange data",
      summary = "Reads the Currency Exchange data", tags = {
      "Reads the Currency Exchange data"})
  @GetMapping(value = "/report")
  public Mono<ResponseEntity<ExchangeRateDetails>> readCurrencyExchangeData() {
    log.debug("Started Read the report data ");
    return exchangeRateService.readReportData(FILE_PATH).map(ResponseEntity.status(HttpStatus.OK)::body);
  }

}
