package com.mbzshajib.assignment.analytic.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TickDto {
    private String instrument;
    private Double price;
    private String timeKey;
}
