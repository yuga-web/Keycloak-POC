import { User } from './../models/user';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  userURL = 'http://localhost:8080/user/';

  httpOptions = { headers: new HttpHeaders({'Content-Type' : 'application/json'})};

  constructor(private httpClient: HttpClient) { }

  public create(user: User): Observable<any> {
    return this.httpClient.post<any>(this.userURL + 'create', user, this.httpOptions);
  }
  public updateUser(id:any,user: any): Observable<any> {
    return this.httpClient.put<any>(`${this.userURL}updateuser/${id}`, user, this.httpOptions);
  }
  public view(): Observable<any> {
    return this.httpClient.get<any>(this.userURL + 'viewusers', this.httpOptions);
  }
  public delete(id:any): Observable<any> {
    return this.httpClient.delete<any>(`${this.userURL}deleteuser/${id}`,this.httpOptions);
  }
  public getUserById(id:any): Observable<any> {
    return this.httpClient.get<any>(`${this.userURL}viewuser/${id}`, this.httpOptions);
  }
  public getRolesById(name:any): Observable<any> {
    return this.httpClient.get<any>(`${this.userURL}getRoleById/${name}`, this.httpOptions);
  }
  public viewRoles(): Observable<any> {
    return this.httpClient.get<any>(this.userURL + 'viewRoles', this.httpOptions);
  }
  public createRole(role: any): Observable<any> {
    return this.httpClient.post<any>(this.userURL + 'createRole', role, this.httpOptions);
  }
  public updateRole(name:any,role: any): Observable<any> {
    return this.httpClient.put<any>(`${this.userURL}updateRole/${name}`, role, this.httpOptions);
  }
  public deleteRoles(name:any): Observable<any> {
    return this.httpClient.delete<any>(`${this.userURL}deleteRole/${name}`,this.httpOptions);
  }
}
