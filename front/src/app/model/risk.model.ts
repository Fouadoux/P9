export interface DiabetesRisk {
    patientId: string;
    riskLevel: 'NONE' | 'BORDERLINE' | 'IN_DANGER' | 'EARLY_ONSET';
  }