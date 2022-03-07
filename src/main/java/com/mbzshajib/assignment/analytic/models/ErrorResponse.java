package com.mbzshajib.assignment.analytic.models;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ErrorResponse {
    private String errorCode;
    private String errorMessage;
}
