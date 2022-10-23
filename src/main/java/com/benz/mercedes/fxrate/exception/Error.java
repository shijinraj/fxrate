package com.benz.mercedes.fxrate.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Error {

  //spanId
  @Schema(name = "errorId", format = "String", description = "unique Id for tracing the logs")
  private String id;

  private String message;

  private Integer code;

  private List<String> details;

}
