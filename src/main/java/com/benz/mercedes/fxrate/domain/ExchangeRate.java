
package com.benz.mercedes.fxrate.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRate {

  @JsonProperty("base_code")
  private String baseCode;
  @JsonProperty("conversion_rates")
  private ConversionRates conversionRates;

  @JsonProperty("conversion_rate")
  private Double conversionRate;

}
