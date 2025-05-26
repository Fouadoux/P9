export interface AppUserResponse{
    id: number
    firstName:string
    lastName:string
    email:string
    role:string
    active:boolean
    password:string
    status: 'ACTIF' | 'INACTIF'
}