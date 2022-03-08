package com.mbzshajib.assignment.analytic.endpoints.excptionresolver;

import com.mbzshajib.assignment.analytic.application.exception.FutureRequestException;
import com.mbzshajib.assignment.analytic.application.exception.NoProcessingRequiredException;
import com.mbzshajib.assignment.analytic.application.exception.QueueOverFlowException;
import com.mbzshajib.assignment.analytic.endpoints.controller.TickController;
import com.mbzshajib.assignment.analytic.endpoints.controller.TickStatisticController;
import com.mbzshajib.assignment.analytic.models.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.mbzshajib.assignment.analytic.application.utils.ErrorCodes.Codes.*;
import static com.mbzshajib.assignment.analytic.application.utils.ErrorCodes.Messages.UNKNOWN_ERROR_MSG;

@Slf4j
@ControllerAdvice(basePackageClasses = {
        TickController.class,
        TickStatisticController.class
})
public class TickControllerAdvice {


    @ResponseBody
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(QueueOverFlowException.class)
    public void handleServerOverloadedException(QueueOverFlowException exception) {
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ExceptionHandler(NoProcessingRequiredException.class)
    public void handleNoProcessingRequiredException(NoProcessingRequiredException exception) {
    }


    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FutureRequestException.class)
    public ErrorResponse handleFutureRequestException(FutureRequestException exception) {
        log.error("exception occurred. ", exception);
        return ErrorResponse.builder()
                .errorCode(TICK_FROM_FUTURE)
                .errorMessage(exception.getMessage())
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {

        return ErrorResponse.builder()
                .errorCode(INVALID_REQUEST_DATA)
                .errorMessage(exception.getFieldError() == null ? "Invalid parameters" : exception.getFieldError().getDefaultMessage())
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleCommonError(Exception exception) {
        log.error("Unknown exception occurred. ", exception);
        return ErrorResponse.builder()
                .errorCode(UNKNOWN_ERROR)
                .errorMessage(UNKNOWN_ERROR_MSG)
                .build();
    }
}
