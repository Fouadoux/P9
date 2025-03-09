package com.glucovision.gateway.clients;

import org.springframework.cloud.openfeign.FeignClient;
import com.glucovision.gateway.dto.HistoryDTO;
import com.glucovision.gateway.dto.CommentDTO;

import org.springframework.web.bind.annotation.*;

@FeignClient(name = "HISTORY-SERVICE")
public interface HistoryServiceClient {

    @GetMapping("/history/{patientId}")
    HistoryDTO getHistoryByPatientId(@PathVariable("patientId") String patientId);

    @PostMapping("/history/{patientId}/add-comment")
    HistoryDTO addCommentToHistory(@PathVariable("patientId") String patientId, @RequestBody CommentDTO comment);

}
