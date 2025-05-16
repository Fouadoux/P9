package com.glucovision.patientservice.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Enum representing the gender of a patient.
 * <p>
 * This enum supports two values:
 * <ul>
 *   <li>{@code MALE}</li>
 *   <li>{@code FEMALE}</li>
 * </ul>
 *
 * It is deserialized using a custom {@link GenderDeserializer} to handle case-insensitive or
 * user-friendly string values from JSON (e.g. "male", "Male", "FEMALE", etc.).
 */
@JsonDeserialize(using = GenderDeserializer.class)
public enum Gender {
    MALE,
    FEMALE;
}
