package com.glucovision.noteservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "patient-service", url = "${patient-service.url}")
public interface PatientClient {

    @GetMapping("/api/patients/{id}/exists")
    boolean isActivePatient(@PathVariable String id);
}
