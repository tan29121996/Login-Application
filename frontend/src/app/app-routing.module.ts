import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './component/login/login.component';
import { RegisterComponent } from './component/register/register.component';
import { ProfileComponent } from './component/profile/profile.component';
import { ManageUsersComponent } from './component/manage-users/manage-users.component';
import { userguardGuard } from './guards/userguard.guard';
import { adminguardGuard } from './guards/adminguard.guard';
import { loginguardGuard } from './guards/loginguard.guard';

const routes: Routes = [
  { path: 'login', component: LoginComponent, canActivate: [loginguardGuard] },
  { path: 'signup', component: RegisterComponent, canActivate: [loginguardGuard] },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'users/:id', component: ProfileComponent, canActivate: [userguardGuard] },
  { path: 'restricted', component: ManageUsersComponent, canActivate: [adminguardGuard] },
  { path: '**', redirectTo: '/login', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
