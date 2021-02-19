import { Component, OnInit } from '@angular/core';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-list-roles',
  templateUrl: './list-roles.component.html',
  styleUrls: ['./list-roles.component.css']
})
export class ListRolesComponent implements OnInit {
roles:any;
createRole="new";
  constructor(private userService:UserService) { }

  ngOnInit(): void {
    this.loadRoles();
  }
loadRoles(){
  this.userService.viewRoles().subscribe(data=>
    {this.roles=data;console.log(data)},
    err=>console.log(err)
    );
}
deleteRole(name:any){
  this.userService.deleteRoles(name).subscribe(data=>
    {console.log(data)
      this.loadRoles();
    },
    err => console.log(err)
    );
}
}
