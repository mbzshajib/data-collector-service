package com.mbzshajib.assignment.analytic.excptionresolver;

import com.mbzshajib.assignment.analytic.controller.TickController;
import com.mbzshajib.assignment.analytic.controller.TickStatisticController;
import com.mbzshajib.assignment.analytic.exception.ServerOverloadedException;
import com.mbzshajib.assignment.analytic.model.ErrorResponse;
import com.mbzshajib.assignment.analytic.utils.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice(basePackageClasses = {
        TickController.class,
        TickStatisticController.class
})
public class TickControllerAdvice {
    private String UNKNOWN_ERROR_MSG = "Unknown error happened";

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return ErrorResponse.builder()
                .errorCode(ErrorCodes.INVALID_REQUEST_DATA)
                .errorMessage(exception.getFieldError().getDefaultMessage())
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class})
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException exception) {
        log.error("exception occurred. ", exception);
        return ErrorResponse.builder()
                .errorCode(ErrorCodes.INVALID_REQUEST)
                .errorMessage(exception.getMessage())
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler({ServerOverloadedException.class})
    public void handleServerOverloadedException(ServerOverloadedException exception) {
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public ErrorResponse handleCommonError(Exception exception) {
        log.error("Unknown exception occurred. ", exception);
        return ErrorResponse.builder()
                .errorCode(ErrorCodes.UNKNOWN_ERROR)
                .errorMessage(UNKNOWN_ERROR_MSG)
                .build();
    }
}
