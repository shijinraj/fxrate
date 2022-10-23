package com.benz.mercedes.fxrate.domain.jaxb;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@XmlRootElement(name = "ExchangeRateDetails")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExchangeRateDetails {

  private List<CurrencyChangeDetails> euroCurrencyChanges = null;

  private List<CurrencyChangeDetails> usdCurrencyChanges = null;

  private List<CurrencyChangeDetails> chfCurrencyChanges = null;

  private List<CurrencyChangeDetails> gbpCurrencyChanges = null;




}
