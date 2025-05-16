package com.glucovision.noteservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client used to communicate with the Patient Service.
 * This interface provides access to patient-related operations exposed
 * by the remote Patient Service, such as checking if a patient is active.
 *
 * <p>Typical usage:</p>
 * <pre>
 * boolean isActive = patientClient.isActivePatient("some-uuid");
 * </pre>
 *
 * <p>
 * The {@code patient-service.url} property must be defined in your application's configuration
 * to resolve the service endpoint.
 * </p>
 *
 * <p><b>Logs:</b></p>
 * To add logs, use a wrapper or an interceptor since Feign interfaces do not support @Slf4j directly.
 * Example wrapper usage:
 * <pre>
 * {@code
 * log.info("[PATIENT CLIENT] Checking if patient with ID {} is active", id);
 * boolean result = patientClient.isActivePatient(id);
 * log.info("[PATIENT CLIENT] Patient active status: {}", result);
 * }
 * </pre>
 *
 * @author GlucoVision
 */
@FeignClient(name = "patient-service", url = "${patient-service.url}")
public interface PatientClient {

    /**
     * Calls the Patient Service to determine if the patient with the given ID is active.
     *
     * @param id the unique identifier of the patient (UUID format recommended)
     * @return true if the patient exists and is active, false otherwise
     */
    @GetMapping("/api/patients/{id}/exists")
    boolean isActivePatient(@PathVariable String id);
}
