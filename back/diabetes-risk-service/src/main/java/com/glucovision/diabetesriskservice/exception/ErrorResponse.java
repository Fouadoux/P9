package com.glucovision.diabetesriskservice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Standard structure for sending error responses from the API.
 * <p>
 * This DTO encapsulates a simple error message that can be returned to the client
 * in case of failures such as validation errors, missing resources, or internal exceptions.
 */
@Data
@AllArgsConstructor
public class ErrorResponse {

    /**
     * A short, descriptive error message.
     */
    private String error;
}
