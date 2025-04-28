package com.glucovision.patientservice.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
class PatientTest {

    @ParameterizedTest
    @CsvSource({
            "2020-01-15, 2020-01-15",
            "15-01-2020, 2020-01-15",
            "1995-12-31, 1995-12-31",
            "31-12-1995, 1995-12-31"
    })
    void parseDate_shouldParseValidDates(String inputDate, String expectedDate) {
        // Arrange
        Patient patient = new Patient();
        LocalDate expected = LocalDate.parse(expectedDate);

        // Act
        LocalDate result = patient.parseDate(inputDate);

        // Assert
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "01/15/2020",       // Format MM/dd/yyyy non supporté
            "2020/01/15",       // Format yyyy/MM/dd non supporté
            "15-Jan-2020",      // Format avec mois textuel
            "invalid-date",     // Chaîne invalide
            "123456",           // Nombre
            ""                  // Chaîne vide
    })
    void parseDate_shouldThrowExceptionForInvalidFormats(String invalidDate) {
        // Arrange
        Patient patient = new Patient();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            patient.parseDate(invalidDate);
        }, "Devrait lever une exception pour le format invalide: " + invalidDate);
    }

    @Test
    void parseDate_shouldBeUsedInConstructors() {
        // Arrange
        String dateStr = "15-05-1990";
        LocalDate expectedDate = LocalDate.of(1990, 5, 15);

        // Act
        Patient patient = new Patient("John", "Doe", dateStr, Gender.MALE, "Address", "Phone");

        // Assert
        assertEquals(expectedDate, patient.getBirthDate());
    }

}