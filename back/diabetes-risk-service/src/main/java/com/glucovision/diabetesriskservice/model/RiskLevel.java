package com.glucovision.diabetesriskservice.model;

/**
 * Enum representing the possible levels of diabetes risk for a patient.
 */
public enum RiskLevel {

    /**
     * No signs of diabetes risk detected.
     */
    NONE,

    /**
     * Limited risk level; some early signs may be present but not alarming.
     */
    BORDERLINE,

    /**
     * Significant risk detected; the patient is in a dangerous range.
     */
    IN_DANGER,

    /**
     * Clear early signs of diabetes; early onset likely.
     */
    EARLY_ONSET
}
