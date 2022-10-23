
package com.benz.mercedes.fxrate.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "USD",
    "GBP",
    "EUR",
    "CHF"
})
@Data
public class ConversionRates {

    @JsonProperty("USD")
    private Double usd;

    @JsonProperty("GBP")
    private Double gbp;

    @JsonProperty("EUR")
    private Double eur;

    @JsonProperty("CHF")
    private Double chf;

}
