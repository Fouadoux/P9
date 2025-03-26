package com.glucovision.diabetesriskservice.client;

import com.glucovision.diabetesriskservice.dto.PatientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("Patient-service")
public interface PatientClient {

    @GetMapping("/patients/{id}")
    PatientDto getPatientById(@PathVariable Long id);

}
