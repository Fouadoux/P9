package com.glucovision.patientservice.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Custom deserializer for the {@link Gender} enum.
 * <p>
 * Accepts case-insensitive values like "male", "MALE", "Female", etc.
 * Throws an {@link IllegalArgumentException} if the value is unrecognized.
 */
@Slf4j
public class GenderDeserializer extends JsonDeserializer<Gender> {

    @Override
    public Gender deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText().trim().toUpperCase();

        log.debug("Deserializing gender value: {}", value);

        return switch (value) {
            case "MALE" -> Gender.MALE;
            case "FEMALE" -> Gender.FEMALE;
            default -> throw new IllegalArgumentException("Unknown gender: " + value);
        };
    }
}