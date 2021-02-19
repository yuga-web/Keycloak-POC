import { Component, OnInit } from '@angular/core';
import {UserService} from 'src/app/services/user.service';
@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css']
})
export class ListComponent implements OnInit {

  constructor(private userService:UserService) { }
users:any=[];
  ngOnInit(): void {
    this.loadUsers();
  }
loadUsers(){
this.userService.view().subscribe(data=>
  {this.users=data;console.log(data)
  },
  err => console.log(err)
  );
}
deleteUser(id:any){
  this.userService.delete(id).subscribe(data=>
    {console.log(data)
      this.loadUsers();
    },
    err => console.log(err)
    );
}
}
