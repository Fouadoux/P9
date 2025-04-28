package com.glucovision.diabetesriskservice.client;

import com.glucovision.diabetesriskservice.dto.PatientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-service", url = "${patient-service.url}")
public interface PatientClient {

    @GetMapping("/api/patients/{id}")
    PatientDto getPatientById(@PathVariable String id);

}
