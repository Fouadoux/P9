import { Gender } from "./gender.model";

export interface Patient{
    uid:string;
    firstName:string;
    lastName:string;
    birthDate:Date|null;
    gender:Gender;
    address:string;
    phone:string;
    active: boolean;
    status: 'ACTIF' | 'INACTIF';  
  }