package com.glucovision.gateway.clients;

import com.glucovision.gateway.dto.PatientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "Patient-service")
public interface PatientServiceClient {

    @GetMapping("/patients")
    List<PatientDTO> getAllPatients();

    @GetMapping("/patients/{id}")
    PatientDTO getPatientById(@PathVariable("id") String id);

    @PostMapping("/patients")
    PatientDTO createPatient(@RequestBody PatientDTO patientDTO);


}
