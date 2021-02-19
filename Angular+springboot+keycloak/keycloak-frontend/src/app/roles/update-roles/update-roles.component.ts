import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { LoginService } from 'src/app/services/login.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-update-roles',
  templateUrl: './update-roles.component.html',
  styleUrls: ['./update-roles.component.css']
})
export class UpdateRolesComponent implements OnInit {
roles:any
roleName:any;
roleForm:FormGroup;
  constructor(public fb: FormBuilder, private userService: UserService, private loginService: LoginService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.roleName = this.route.snapshot.params['name'];
    this.roleForm = this.fb.group({
      name: ['', [Validators.required]],
      description: ['', [Validators.required]],
      compositeRoles: [false]
    });
    if(this.roleName!='new'){
    this.getRoles();
    }
  }
  getRoles() {
    this.userService.getRolesById(this.roleName).subscribe(data => {
      this.roles = data;
      this.roleForm.patchValue(data);
      console.log(data);
    }, err => console.log(err)
    );
  }
  onSubmit() {
    console.log(this.roleForm.value);
    if(this.roleName!='new'){
   this.userService.updateRole(this.roleName,this.roleForm.value).subscribe(
     data=>{console.log(data)},
     err=>console.log(err));
   }else{
    this.userService.createRole(this.roleForm.value).subscribe(
      data=>{console.log(data)},
      err=>console.log(err)); 
   }
  }
}
