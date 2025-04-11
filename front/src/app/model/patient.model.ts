import { Gender } from "./gender.model";

export interface Patient{
    id:number;
    firstName:string;
    lastName:string;
    birthDate:string;
    gender:Gender;
    address:string;
    phone:string;
    active: boolean;
    status: 'ACTIF' | 'INACTIF';  
  }