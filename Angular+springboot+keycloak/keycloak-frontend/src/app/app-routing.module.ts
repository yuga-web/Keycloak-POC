import { SignupGuard } from './guards/signup.guard';
import { FooGuard } from './guards/foo.guard';
import { SignupComponent } from './signup/signup.component';
import { CreateComponent } from './foo/create/create.component';
import { UpdateComponent } from './foo/update/update.component';
import { DetailComponent } from './foo/detail/detail.component';
import { ListaComponent } from './foo/lista/lista.component';
import { HomeComponent } from './home/home.component';
import { ListComponent } from './users/list/list.component';
import { UpdateuserComponent } from './users/updateuser/updateuser.component';
import { ListRolesComponent } from './roles/list-roles/list-roles.component';
import { UpdateRolesComponent } from './roles/update-roles/update-roles.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';


const routes: Routes = [

  {path: '', component: HomeComponent},
  {path: 'lista', component: ListaComponent, canActivate: [FooGuard], data: {requiredRoles: ['admin', 'user']}},
  {path: 'detail/:id', component: DetailComponent, canActivate: [FooGuard], data: {requiredRoles: ['admin', 'user']}},
  {path: 'update/:id', component: UpdateComponent, canActivate: [FooGuard], data: {requiredRoles: ['admin']}},
  {path: 'create', component: CreateComponent, canActivate: [FooGuard], data: {requiredRoles: ['admin']}},
  {path: 'viewusers', component: ListComponent, canActivate: [FooGuard], data: {requiredRoles: ['admin']}},
  {path: 'updateusers/:id', component: UpdateuserComponent, canActivate: [FooGuard], data: {requiredRoles: ['admin']}},
  {path: 'viewroles', component: ListRolesComponent, canActivate: [FooGuard], data: {requiredRoles: ['admin']}},
  {path: 'updateRole/:name', component: UpdateRolesComponent, canActivate: [FooGuard], data: {requiredRoles: ['admin']}},
  {path: 'signup', component: SignupComponent, canActivate: [SignupGuard]},
  {path: '**', redirectTo: '', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
