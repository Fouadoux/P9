package com.glucovision.diabetesriskservice.client;

import com.glucovision.diabetesriskservice.dto.PatientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client interface for communicating with the Patient Service.
 * <p>
 * This client allows the Diabetes Risk Service to retrieve detailed information
 * about a specific patient using their unique identifier.
 * </p>
 */
@FeignClient(name = "patient-service", url = "${patient-service.url}")
public interface PatientClient {

    /**
     * Retrieves patient information by their unique ID.
     *
     * @param id The unique identifier of the patient.
     * @return A {@link PatientDto} containing patient details such as birth date and gender.
     */
    @GetMapping("/api/patients/{id}")
    PatientDto getPatientById(@PathVariable String id);
}
