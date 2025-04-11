export interface AppUserResponse{
    id: number
    firstName:string
    lastName:string
    email:string
    role:string
    active:boolean
    status: 'ACTIF' | 'INACTIF'
}