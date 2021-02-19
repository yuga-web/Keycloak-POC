import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from 'src/app/services/user.service';
import { LoginService } from './../../services/login.service';
@Component({
  selector: 'app-updateuser',
  templateUrl: './updateuser.component.html',
  styleUrls: ['./updateuser.component.css']
})
export class UpdateuserComponent implements OnInit {
  userId: any;
  user: any;
  isAdmin: boolean;
  userForm: FormGroup;
  constructor(public fb: FormBuilder, private userService: UserService, private loginService: LoginService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.isAdmin = this.loginService.getIsAdmin();
    this.userId = this.route.snapshot.params['id'];
    this.userForm = this.fb.group({
      username: ['', [Validators.required]],
      email: ['', [Validators.required]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]]
    });
    this.getUser();

  }
  getUser() {
    this.userService.getUserById(this.userId).subscribe(data => {
      this.user = data;
      this.userForm.patchValue(data);
      console.log(data);
    }, err => console.log(err)
    );
  }
  onSubmit() {
    console.log(this.userForm.value);
   this.userService.updateUser(this.userId,this.userForm.value).subscribe(
     data=>{console.log(data)},
     err=>console.log(err));
    
  }
}
