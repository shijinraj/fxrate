package com.benz.mercedes.fxrate.domain.jaxb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "CurrencyChangeDetails")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "date","value" })
public class CurrencyChangeDetails {

  private Date date;

  private Double value;

}
