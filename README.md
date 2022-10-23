# Coding Challenge
## Description of the challenge
*   Build a simple exchange rates Service. The service should be implemented as a stand-alone
    application and should provide the following features:
## Description
Fetch currency rates from external service every two hours. Sample exchange
service: https://www.exchangerate-api.com/docs/standard-requests
* Create a file which contains certain exchange rates: USD, GBP, EUR, CHF
* File format is in XML
* A daily report is created. Content of the file 
  - Includes any changes of exchange rates
* RESTful endpoints:
  - Trigger manual exchange rate fetching process
  - Read current exchange rate in terms of X/Y. Example USD/EUR
  - Read the report data in terms of base currency: EUR
  - Swagger UI is available for testing - http://localhost:8080/webjars/swagger-ui/index.html

## Setup
* JDK 11 or higher version
* Maven 3.8.3 or higher version
* Run application using command line from the **project directory** - mvn spring-boot:run Or using Docker - docker commands available in the Dockerfile. 
* Swagger URL - http://localhost:8080/webjars/swagger-ui/index.html

## 1. Trigger Currency Exchange Rate Scheduler and Daily Report Scheduler:
* HTTP : GET - /api/exchangerate
* HTTP response : 200
* Currency Exchange Rate Scheduler will be triggered and running in every 2 hour interval. 
* Daily Report Scheduler will be triggered and running in every 24 hour interval.
* Currency Exchange Rate Scheduler invokes the exchangerate-api REST API with Euro as Base Currency.
* Currency Exchange Rate Scheduler compares the latest exchange rates against the store XML rates.
* Currency Exchange Rate Scheduler add the currency rates along with the respective date & time , only if there are any rate changes.
* Currency Exchange Rate Scheduler persist the rate changes as XML file which is compliance with a XSD which is mapped to the JAXB classes.
* Currency Exchange Rate Scheduler uses Flux.interval as as scheduler.
* Daily Report Scheduler reads the Currency Exchange Rates XML file in every 24 hour interval and generate the report.
* Daily Report Scheduler uses Flux.interval as as scheduler.
## 2. Read the report data in terms of base currency: EUR:
* HTTP : GET - /api/exchangerate/daily-report
* HTTP response : Exchange Rate Details of EUR, USD, GBP and CHF in terms of base currency: EUR
* Daily Report Scheduler reads the Currency Exchange Rate changes and Creates a daily report as XML file. 
* We can produce XML by changing the produces as MediaType.APPLICATION_XML_VALUE .
* Sample HTTP response :
```json
[
  {
    "euroCurrencyChanges": [
      {
        "date": "2022-10-23T13:20:18.535Z",
        "value": 1
      }
    ],
    "usdCurrencyChanges": [
      {
        "date": "2022-10-23T13:20:18.535Z",
        "value": 0.99
      }
    ],
    "chfCurrencyChanges": [
      {
        "date": "2022-10-23T13:20:18.535Z",
        "value": 0.98
      }
    ],
    "gbpCurrencyChanges": [
      {
        "date": "2022-10-23T13:20:18.535Z",
        "value": 0.87
      }
    ]
  }
]
```
* HTTP : GET - /api/exchangerate/report
* HTTP response : Exchange Rate Details of EUR, USD, GBP and CHF in terms of base currency: EUR
* Reads the Currency Exchange Rate Scheduler persisted XML file in JSON format.
* We can produce XML by changing the produces as MediaType.APPLICATION_XML_VALUE .
* Sample HTTP response :
```json
[
  {
    "euroCurrencyChanges": [
      {
        "date": "2022-10-23T11:34:23.563+00:00",
        "value": 1
      }
    ],
    "usdCurrencyChanges": [
      {
        "date": "2022-10-23T11:34:23.563+00:00",
        "value": 0.9825
      }
    ],
    "chfCurrencyChanges": [
      {
        "date": "2022-10-23T11:34:23.563+00:00",
        "value": 0.9841
      }
    ],
    "gbpCurrencyChanges": [
      {
        "date": "2022-10-23T11:34:23.563+00:00",
        "value": 0.875
      }
    ]
  }
]
```
## 3. Read current exchange rate in terms of X/Y. Example USD/EUR
* HTTP : GET - /api/exchangerate/pair/{baseCurrency}/{targetCurrency}
* HTTP response : Real time Currency exchange rate

## Application Features
* Spring Boot WebFlux project with Spring cloud open feign,  JUnit 5, Swagger, Mockito, JAXB, actuator and lombok.
* Unit tests (Controller layer, Service Layer and Feign Client tests ) and Integration tests are available.
* Centralised Exception handling using @RestControllerAdvice - please refer the package com.benz.mercedes.fxrate.exception
* Exception example
```json
{
  "id": 172789177, --unique identifier for the exception
  "message": "Bad Request",
  "Integer": "400",
  "more_info": "Invalid inputs"
}
```
* Dockerized  application.

## Assumption
* Currency Exchange Rate Scheduler updates a XML file which contains certain exchange rates: USD, GBP, EUR, CHF. This process takes place every 2 hours.
* Real time call spanned to exchangerate-api to read the current exchange rate in terms of X/Y. 
* Read the report data in terms of base currency: EUR -   This reads the daily report data from the XML file and base currency is  always  EUR.Daily Report Scheduler is configured for the same.
* A daily report should be created. Content of the file should be o Include any changes of exchange rates  - Daily Report Scheduler is configured for the same.
* euroCurrencyChanges will be always "1" since we have taken Euro as base currency.
* 2 pd (person days) of development efforts for the MVP (Minimum Viable Product).

## Improvements
* Implement Quartz or any similar distributed scheduler.
* Implement Spring Cloud OpenFeign fall back mechanism , if the exchangerate-api is unavailable.
* Implement Reactive Feign for a complete  Reactive application.
* Move feign client URL & API Key as a constant in yaml.
* Externalize the XML file location.
* Improve the exception handling in process.
* Improve Unit testing for the XML file creation. Power mock has problems with Junit5 . Hence unit testing is pending for the Currency Exchange Rate Scheduler andDaily Report Scheduler.
* Contract Testing to be done.
