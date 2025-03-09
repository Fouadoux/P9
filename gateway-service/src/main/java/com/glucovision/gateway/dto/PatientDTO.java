package com.glucovision.gateway.dto;

import lombok.Data;

@Data
public class PatientDTO {
    private Long id;
    private String prenom;
    private String nom;
    private String dateNaissance;
    private String genre;
    private String adresse;
    private String telephone;
}
