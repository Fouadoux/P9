package com.glucovision.noteservice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a standardized error response returned by the API.
 * <p>
 * This object is used to wrap error messages in a consistent format.
 * Typically returned in case of bad requests, forbidden actions, or not found errors.
 */
@Data
@AllArgsConstructor
public class ErrorResponse {

    /**
     * The error message describing the cause of the failure.
     */
    private String error;
}