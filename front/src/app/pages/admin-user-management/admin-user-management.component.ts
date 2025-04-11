import { Component, effect, inject, signal } from '@angular/core';
import { appUserService } from '../../services/appUser.service';
import { AppUserResponse } from '../../model/appUserResponse.model';
import { CommonModule } from '@angular/common';
import { UserEditDialogComponent } from '../../Components/user-edit-dialog/user-edit-dialog.component';
import { AppCardComponent } from '../../Components/app-card/app-card.component';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { EditDialogComponent } from '../../Components/edit-dialog/edit-dialog.component';

@Component({
  selector: 'app-admin-user-management',
  imports: [CommonModule,AppCardComponent, MatDialogModule],
  templateUrl: './admin-user-management.component.html',
  styleUrl: './admin-user-management.component.css'
})
export class AdminUserManagementComponent {

  private appUserService=inject(appUserService);
  users = signal<AppUserResponse[]>([]);
  isLoading = signal<boolean>(true); 
  public fb= inject(FormBuilder);
  selectedUser = signal<AppUserResponse | null>(null);
  showEditDialog = signal(false);
  private dialog=inject(MatDialog)

  validators=Validators;

  constructor() {
    effect(() => {
      this.appUserService.allUser().subscribe(data => {
        this.users.set(data);
        this.isLoading.set(false); 
      }, error => {
        console.error('Erreur lors du chargement des utilisateurs', error);
        this.isLoading.set(false);
      });
    });
  }

  

onEditUser(user: AppUserResponse) {
  const dialogRef=this.dialog.open(EditDialogComponent<AppUserResponse>,{
    data :{
      data: user,
      type: 'user',
      title: 'Modifier un utilisateur'
    },
    width: '500px'
  });

  dialogRef.afterClosed().subscribe((result:AppUserResponse|null)=>{
    if(result){
      this.handleUserSave(result);
    }
  });
}

handleUserSave(updatedUser: AppUserResponse) {
  this.appUserService.updateUser(updatedUser).subscribe({
    next: (res) => {
      const users = this.users().map(u =>
        u.id === res.id ? res : u
      );
      this.users.set(users);
      this.showEditDialog.set(false);
    },
    error: (err) => {
      console.error('❌ Erreur lors de la mise à jour', err);
    }
  });
}

handleToggleActive(user: AppUserResponse) {
  this.appUserService.toggleActiveUser(user.id).subscribe({
    next: (res) => {
      const users = this.users().map(u =>
        u.id === res.id ? res : u
      );
      this.users.set(users);
    },
    error: (err) => {
      console.error('❌ Erreur lors du changement de statut', err);
    }
  });
}




 

}
