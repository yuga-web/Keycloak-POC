package com.czetsuya.keycloak.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.RoleRepresentation.Composites;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.czetsuya.keycloak.KeycloakAdminClientConfig;
import com.czetsuya.keycloak.KeycloakAdminClientUtils;
import com.czetsuya.keycloak.KeycloakPropertyReader;
import com.czetsuya.security.CurrentUser;
import com.czetsuya.security.CurrentUserProvider;
import com.czetsuya.security.RealmRole;



/**
 * @author Edward P. Legaspi | czetsuya@gmail.com
 * 
 * @version 0.0.1
 * @since 0.0.1
 */
@Service
public class KeycloakAdminClientService {

    @Value("${keycloak.resource}")
    private String keycloakClient;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @Autowired
    private KeycloakPropertyReader keycloakPropertyReader;

    public List<String> getCurrentUserRoles() {
        return currentUserProvider.getCurrentUser().getRoles();
    }

    public Object getUserProfileOfLoggedUser() {

        @SuppressWarnings("unchecked")
        KeycloakPrincipal<RefreshableKeycloakSecurityContext> principal = (KeycloakPrincipal<RefreshableKeycloakSecurityContext>) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        KeycloakAdminClientConfig keycloakAdminClientConfig = KeycloakAdminClientUtils.loadConfig(keycloakPropertyReader);
        Keycloak keycloak = KeycloakAdminClientUtils.getKeycloakClient(principal.getKeycloakSecurityContext(), keycloakAdminClientConfig);

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UsersResource usersResource = realmResource.users();
        UserResource userResource = usersResource.get(currentUserProvider.getCurrentUser().getUserId());
        UserRepresentation userRepresentation = userResource.toRepresentation();

        return userRepresentation;
    }
    public Object getAllUser() {

        @SuppressWarnings("unchecked")
        KeycloakPrincipal<RefreshableKeycloakSecurityContext> principal = (KeycloakPrincipal<RefreshableKeycloakSecurityContext>) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        KeycloakAdminClientConfig keycloakAdminClientConfig = KeycloakAdminClientUtils.loadConfig(keycloakPropertyReader);
        Keycloak keycloak = KeycloakAdminClientUtils.getKeycloakClient(principal.getKeycloakSecurityContext(), keycloakAdminClientConfig);

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        List<UserRepresentation> userRepresentation = realmResource.users().list();
        return userRepresentation;
    }
    public String deleteUser(String id) {

        @SuppressWarnings("unchecked")
        KeycloakPrincipal<RefreshableKeycloakSecurityContext> principal = (KeycloakPrincipal<RefreshableKeycloakSecurityContext>) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        KeycloakAdminClientConfig keycloakAdminClientConfig = KeycloakAdminClientUtils.loadConfig(keycloakPropertyReader);
        Keycloak keycloak = KeycloakAdminClientUtils.getKeycloakClient(principal.getKeycloakSecurityContext(), keycloakAdminClientConfig);

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UsersResource usersResource = realmResource.users();
        usersResource.delete(id);
        return "Deleted the User";
    }
    
    public Object[] createUser(CurrentUser user){
    	 @SuppressWarnings("unchecked")
         KeycloakPrincipal<RefreshableKeycloakSecurityContext> principal = (KeycloakPrincipal<RefreshableKeycloakSecurityContext>) SecurityContextHolder.getContext()
                 .getAuthentication().getPrincipal();
         KeycloakAdminClientConfig keycloakAdminClientConfig = KeycloakAdminClientUtils.loadConfig(keycloakPropertyReader);
         Keycloak keycloak = KeycloakAdminClientUtils.getKeycloakClient(principal.getKeycloakSecurityContext(), keycloakAdminClientConfig);

         // Get realm
         RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        ResponseMessage message = new ResponseMessage();
        int statusId = 0;
         try {
             UsersResource usersResource = realmResource.users();
             UserRepresentation userRepresentation = new UserRepresentation();
             userRepresentation.setUsername(user.getUsername());
             userRepresentation.setEmail(user.getEmail());
             userRepresentation.setFirstName(user.getFirstName());
             userRepresentation.setLastName(user.getLastName());
             userRepresentation.setEnabled(true);
             Map<String, List<String>> data = new HashMap<String, List<String>>();
             data.put("realm-management", Arrays.asList("view-realm", "view-users", "manage-users"));
             userRepresentation.setClientRoles(data);
             Response result = usersResource.create(userRepresentation);
             statusId = result.getStatus();

             if(statusId == 201){
                 String path = result.getLocation().getPath();
                 String userId = path.substring(path.lastIndexOf("/") + 1);
                 CredentialRepresentation passwordCredential = new CredentialRepresentation();
                 passwordCredential.setTemporary(false);
                 passwordCredential.setType(CredentialRepresentation.PASSWORD);
                 passwordCredential.setValue(user.getPassword());
                 usersResource.get(userId).resetPassword(passwordCredential);

                 RoleRepresentation roleRepresentation = realmResource.roles().get("CatalogManager").toRepresentation();
                 realmResource.users().get(userId).roles().realmLevel().add(Arrays.asList(roleRepresentation));
                 message.setMessage("user created successfully");
             }else if(statusId == 409){
                 message.setMessage("User already Exist");
             }else{
                 message.setMessage("error creating the user");
             }
         }catch (Exception e){
             e.printStackTrace();
         }

         return new Object[]{statusId, message};
    }
    
    public Object updateUser(String id,CurrentUser user) {

        @SuppressWarnings("unchecked")
        KeycloakPrincipal<RefreshableKeycloakSecurityContext> principal = (KeycloakPrincipal<RefreshableKeycloakSecurityContext>) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        KeycloakAdminClientConfig keycloakAdminClientConfig = KeycloakAdminClientUtils.loadConfig(keycloakPropertyReader);
        Keycloak keycloak = KeycloakAdminClientUtils.getKeycloakClient(principal.getKeycloakSecurityContext(), keycloakAdminClientConfig);

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UsersResource usersResource = realmResource.users();
        UserResource userResource = usersResource.get(id);
        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userResource.update(userRepresentation);
        //Set Password
        CredentialRepresentation passwordCredential = new CredentialRepresentation();
        passwordCredential.setTemporary(false);
        passwordCredential.setType(CredentialRepresentation.PASSWORD);
        passwordCredential.setValue(user.getPassword());
        usersResource.get(id).resetPassword(passwordCredential);
        return userRepresentation;
    }
public String createRoles(RealmRole role) {
	 @SuppressWarnings("unchecked")
     KeycloakPrincipal<RefreshableKeycloakSecurityContext> principal = (KeycloakPrincipal<RefreshableKeycloakSecurityContext>) SecurityContextHolder.getContext()
             .getAuthentication().getPrincipal();
     KeycloakAdminClientConfig keycloakAdminClientConfig = KeycloakAdminClientUtils.loadConfig(keycloakPropertyReader);
     Keycloak keycloak = KeycloakAdminClientUtils.getKeycloakClient(principal.getKeycloakSecurityContext(), keycloakAdminClientConfig);
     RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
     RoleRepresentation roleRepresentation=new RoleRepresentation();
     roleRepresentation.setName(role.getName());
     roleRepresentation.setDescription(role.getDescription());
     roleRepresentation.setComposite(false);
     realmResource.roles().create(roleRepresentation);
     return "Created Roles";

}
public String deleteRoles(String roleName) {
	 @SuppressWarnings("unchecked")
    KeycloakPrincipal<RefreshableKeycloakSecurityContext> principal = (KeycloakPrincipal<RefreshableKeycloakSecurityContext>) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
    KeycloakAdminClientConfig keycloakAdminClientConfig = KeycloakAdminClientUtils.loadConfig(keycloakPropertyReader);
    Keycloak keycloak = KeycloakAdminClientUtils.getKeycloakClient(principal.getKeycloakSecurityContext(), keycloakAdminClientConfig);
    RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
    realmResource.roles().deleteRole(roleName);
    return "Deleted the Specific Role";

}
public Object updateRole(String roleName,RealmRole role) {
	 @SuppressWarnings("unchecked")
	    KeycloakPrincipal<RefreshableKeycloakSecurityContext> principal = (KeycloakPrincipal<RefreshableKeycloakSecurityContext>) SecurityContextHolder.getContext()
	            .getAuthentication().getPrincipal();
	    KeycloakAdminClientConfig keycloakAdminClientConfig = KeycloakAdminClientUtils.loadConfig(keycloakPropertyReader);
	    Keycloak keycloak = KeycloakAdminClientUtils.getKeycloakClient(principal.getKeycloakSecurityContext(), keycloakAdminClientConfig);
	    RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
	    RoleRepresentation roleRepresentation = realmResource.roles().get(roleName).toRepresentation();
	    roleRepresentation.setDescription(role.getDescription());
	    roleRepresentation.setComposite(role.getCompositeRoles());
	    Composites composites=new Composites();
	    Map<String, List<String>> client = new HashMap<String, List<String>>();
	    client.put("realm-management", Arrays.asList("view-realm", "view-users", "manage-users"));
	    composites.setClient(client);    
	    roleRepresentation.setComposites(composites);
	    realmResource.roles().get(roleName).update(roleRepresentation);
	    RoleRepresentation roleRepresentations = realmResource.roles().get(roleName).toRepresentation();
	    return roleRepresentations;
}
}
