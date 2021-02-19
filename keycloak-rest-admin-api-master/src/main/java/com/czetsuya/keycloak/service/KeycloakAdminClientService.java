package com.czetsuya.keycloak.service;

import java.util.Arrays;
import java.util.List;

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
    
    public Object[] createUser(CurrentUser user){
        ResponseMessage message = new ResponseMessage();
        int statusId = 0;
         try {
             UsersResource usersResource = getUsersResource();
             UserRepresentation userRepresentation = new UserRepresentation();
             userRepresentation.setUsername(user.getUsername());
             userRepresentation.setEmail(user.getEmail());
             userRepresentation.setFirstName(user.getFirstName());
             userRepresentation.setLastName(user.getLastName());
             userRepresentation.setEnabled(true);

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

                 RealmResource realmResource = getRealmResource();
                 RoleRepresentation roleRepresentation = realmResource.roles().get("CatalogManager").toRepresentation();
                 realmResource.users().get(userId).roles().realmLevel().add(Arrays.asList(roleRepresentation));
                 message.setMessage("usuario creado con éxito");
             }else if(statusId == 409){
                 message.setMessage("ese usuario ya existe");
             }else{
                 message.setMessage("error creando el usuario");
             }
         }catch (Exception e){
             e.printStackTrace();
         }

         return new Object[]{statusId, message};
    }

    private RealmResource getRealmResource(){
        Keycloak kc = KeycloakBuilder.builder().serverUrl("http://localhost:8081/auth").realm("master").username("Rubix")
                .password("Rubix").clientId("admin-cli").resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();
        return kc.realm("balambgarden");
    }

 
    private UsersResource getUsersResource(){
        RealmResource realmResource = getRealmResource();
        return realmResource.users();
    }
}
