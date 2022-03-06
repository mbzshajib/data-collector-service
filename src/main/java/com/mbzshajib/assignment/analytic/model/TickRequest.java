package com.mbzshajib.assignment.analytic.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class TickRequest {
    @NotNull(message = "instrument can not be null")
    @Size(min = 1, max = 256, message = "instrument can not be empty")
    private String instrument;
    @NotNull(message = "price can not be null")
    private Double price;
    @NotNull(message = "timestamp can not be null")
    private Long timestamp;
}
