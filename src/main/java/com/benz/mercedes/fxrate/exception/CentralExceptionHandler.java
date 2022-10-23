package com.benz.mercedes.fxrate.exception;

import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Slf4j
@RestControllerAdvice
public class CentralExceptionHandler {

  @ExceptionHandler(Exception.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  public final Error handleAnyExceptions(Exception exception) {

    String uniqueID = getRandomId();
    log.error("ErrorId - {} Inside handleAnyExceptions - ", uniqueID, exception);
    return Error.builder()
        .id(uniqueID)
        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
        .details(Collections.singletonList(Optional.of(exception)
            .map(Exception::getCause)
            .map(Throwable::getMessage).orElseGet(exception::getMessage)))
        .build();
  }

  @ExceptionHandler(FeignException.class)
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  public final Error handleFeignException(
      FeignException feignException) {

    String uniqueID = getRandomId();
    log.error("ErrorId - {} Inside handleFeignException - ", uniqueID,
        feignException);
    // Extracting detailed error message
    String detailedErrorMessage = Optional.of(feignException)
        .map(FeignException::getCause)
        .map(Throwable::getMessage).orElse(null);
    return Error.builder()
        .id(uniqueID)
        .code(HttpStatus.NOT_FOUND.value())
        .message(HttpStatus.NOT_FOUND.getReasonPhrase())
        .details(Collections.singletonList(detailedErrorMessage))
        .build();
  }

  @ExceptionHandler(ServerWebInputException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public final Error handleServerWebInputException(
      ServerWebInputException serverWebInputException) {

    String uniqueID = getRandomId();
    log.error("ErrorId - {} Inside handleServerWebInputException - ", uniqueID,
        serverWebInputException);
    // Extracting detailed error message
    String detailedErrorMessage = Optional.of(serverWebInputException)
        .map(ServerWebInputException::getCause)
        .map(Throwable::getMessage).orElse(null);
    return Error.builder()
        .id(uniqueID)
        .code(HttpStatus.BAD_REQUEST.value())
        .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
        .details(Collections.singletonList(detailedErrorMessage))
        .build();
  }

  @ExceptionHandler(UnsupportedMediaTypeStatusException.class)
  @ResponseStatus(value = HttpStatus.UNSUPPORTED_MEDIA_TYPE)
  public final Error handleUnsupportedMediaTypeException(
      UnsupportedMediaTypeStatusException unsupportedMediaTypeStatusException) {

    String uniqueID = getRandomId();
    log.error("ErrorId - {} Inside handleUnsupportedMediaTypeException - ", uniqueID,
        unsupportedMediaTypeStatusException);
    return Error.builder()
        .id(uniqueID)
        .code(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
        .message(HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase())
        .details(Collections.singletonList(String.format(
            "Content type %s not supported.",
            unsupportedMediaTypeStatusException.getContentType())))
        .build();
  }


  @ExceptionHandler(WebExchangeBindException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public Error handleWebExchangeBindException(
      WebExchangeBindException webExchangeBindException) {

    String uniqueID = getRandomId();
    log.error("ErrorId - {} Inside handleWebExchangeBindException - ", uniqueID,
        webExchangeBindException);
    final List<String> details = new ArrayList<>();
    webExchangeBindException.getBindingResult().getFieldErrors().forEach(
        error -> details.add(error.getField() + ":" + error.getDefaultMessage()));
    Collections.sort(details);
    return Error.builder()
        .id(uniqueID)
        .code(HttpStatus.BAD_REQUEST.value())
        .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
        .details(details)
        .build();

  }

  public String getRandomId() {
    return UUID.randomUUID().toString();
  }
}
