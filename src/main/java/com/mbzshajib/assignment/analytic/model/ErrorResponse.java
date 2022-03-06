package com.mbzshajib.assignment.analytic.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author: Zaman Shajib
 * @email: md.shajib@bKash.com
 * Created on 3/6/22 at 9:37 AM.
 */
@Data
@Builder
public class ErrorResponse {
    private String errorCode;
    private String errorMessage;
}
