package com.tutorial.keycloakbackend.controller;

import com.tutorial.keycloakbackend.service.*;
import com.tutorial.keycloakbackend.security.CurrentUser;
import com.tutorial.keycloakbackend.security.RealmRole;
import com.tutorial.keycloakbackend.dto.ResponseMessage;
import com.tutorial.keycloakbackend.model.User;
import com.tutorial.keycloakbackend.service.KeycloakService;

import java.util.Collection;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin("http://localhost:4200")
public class UserController {

    @Autowired
    private KeycloakService keycloakService;

    @PostMapping("/create")
    public ResponseEntity<ResponseMessage> create(@RequestBody User user){
        Object[] obj = keycloakService.createUser(user);
        int status = (int) obj[0];
        ResponseMessage message = (ResponseMessage) obj[1];
        return ResponseEntity.status(status).body(message);
    }
    @Autowired
    private KeycloakAdminClientService keycloakAdminClientService;

    @GetMapping(path = "/hello")
    public String hello() {
        return "Hello World!";
    }

    @GetMapping(path = "/roles")
    public Collection<String> rolesOfCurrentUser() {
        return keycloakAdminClientService.getCurrentUserRoles();
    }

    @GetMapping(path = "/profile")
    public Object profileOfCurrentUser() {
        return keycloakAdminClientService.getUserProfileOfLoggedUser();
    }
    @GetMapping("/viewusers")
    public Object allUser() {
        return keycloakService.getAllUser();
    }
    @GetMapping("/viewuser/{id}")
    public Object getUserById(@PathVariable String id) {
        return keycloakService.getUserById(id);
    }
   
  
    @PutMapping("/updateuser/{id}")
    public Object updateUser(@PathVariable String id, @RequestBody CurrentUser user ) {
        return keycloakService.updateUser(id,user);
    }
    @DeleteMapping("/deleteuser/{id}")
    public String deleteUser( @PathVariable String id ) {
        return keycloakService.deleteUser(id);
    }
    @GetMapping("/viewRoles")
    public Object allRoles() {
        return keycloakService.getAllRoles();
    }
    @GetMapping("/getRoleById/{roleName}")
    public Object getRoleById(@PathVariable String roleName) {
        return keycloakService.getRoleById(roleName);
    }
    @PostMapping("/createRole")
    public String createRoles(@RequestBody RealmRole role) {
    	return keycloakService.createRoles(role);
    }
    @PutMapping("/updateRole/{roleName}")
    public Object updateRole(@PathVariable String roleName, @RequestBody RealmRole role ) {
        return keycloakService.updateRole(roleName,role);
    }
    @DeleteMapping("/deleteRole/{roleName}")
    public String deleteRoles(@PathVariable String roleName) {
    	return keycloakService.deleteRoles(roleName);
    }
}
