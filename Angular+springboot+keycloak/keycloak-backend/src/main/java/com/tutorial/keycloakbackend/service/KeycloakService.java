package com.tutorial.keycloakbackend.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.RoleRepresentation.Composites;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tutorial.keycloakbackend.dto.ResponseMessage;
import com.tutorial.keycloakbackend.model.User;
import com.tutorial.keycloakbackend.security.CurrentUser;
import com.tutorial.keycloakbackend.security.RealmRole;

@Service
public class KeycloakService {

	@Value("${keycloak.auth-server-url}")
	private String server_url;

	@Value("${keycloak.realm}")
	private String realm;

	public Object[] createUser(User user) {
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

			if (statusId == 201) {
				String path = result.getLocation().getPath();
				String userId = path.substring(path.lastIndexOf("/") + 1);
				CredentialRepresentation passwordCredential = new CredentialRepresentation();
				passwordCredential.setTemporary(false);
				passwordCredential.setType(CredentialRepresentation.PASSWORD);
				passwordCredential.setValue(user.getPassword());
				usersResource.get(userId).resetPassword(passwordCredential);

				RealmResource realmResource = getRealmResource();
				RoleRepresentation roleRepresentation = realmResource.roles().get("realm-user").toRepresentation();
				realmResource.users().get(userId).roles().realmLevel().add(Arrays.asList(roleRepresentation));
				message.setMessage("usuario creado con éxito");
			} else if (statusId == 409) {
				message.setMessage("ese usuario ya existe");
			} else {
				message.setMessage("error creando el usuario");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new Object[] { statusId, message };
	}

	public Object getAllUser() {

		// Get realm
		RealmResource realmResource = getRealmResource();
		List<UserRepresentation> userRepresentation = realmResource.users().list();
		return userRepresentation;
	}
	public Object getUserById(String id) {

		// Get realm
		RealmResource realmResource = getRealmResource();
		UserResource userResource = realmResource.users().get(id);
		UserRepresentation userRepresentation = userResource.toRepresentation();
		return userRepresentation;
	}
	public Object getAllRoles() {

		// Get realm
		RealmResource realmResource = getRealmResource();
		List<RoleRepresentation> roleRepresentation = realmResource.roles().list();
		return roleRepresentation;
	}
	public Object getRoleById(String roleName) {

		// Get realm
		RealmResource realmResource = getRealmResource();
		RoleRepresentation roleRepresentation = realmResource.roles().get(roleName).toRepresentation();
		return roleRepresentation;
	}

	public Object updateUser(String id, CurrentUser user) {

		// Get realm
		RealmResource realmResource = getRealmResource();
		UsersResource usersResource = realmResource.users();
		UserResource userResource = usersResource.get(id);
		UserRepresentation userRepresentation = userResource.toRepresentation();
		userRepresentation.setEmail(user.getEmail());
		userRepresentation.setFirstName(user.getFirstName());
		userRepresentation.setLastName(user.getLastName());
		userResource.update(userRepresentation);
		
		return userRepresentation;
	}

	public String deleteUser(String id) {

		// Get realm
		RealmResource realmResource = getRealmResource();
		UsersResource usersResource = realmResource.users();
		usersResource.delete(id);
		return "Deleted the User";
	}

	public String createRoles(RealmRole role) {
		RealmResource realmResource = getRealmResource();
		RoleRepresentation roleRepresentation = new RoleRepresentation();
		roleRepresentation.setName(role.getName());
		roleRepresentation.setDescription(role.getDescription());
		roleRepresentation.setComposite(false);
		realmResource.roles().create(roleRepresentation);
		return "Created Roles";

	}

	public String deleteRoles(String roleName) {
		RealmResource realmResource = getRealmResource();
		realmResource.roles().deleteRole(roleName);
		return "Deleted the Specific Role";

	}

	public Object updateRole(String roleName, RealmRole role) {
		RealmResource realmResource = getRealmResource();
		RoleRepresentation roleRepresentation = realmResource.roles().get(roleName).toRepresentation();
		roleRepresentation.setDescription(role.getDescription());
		roleRepresentation.setComposite(role.getCompositeRoles());
		Composites composites = new Composites();
		Map<String, List<String>> client = new HashMap<String, List<String>>();
		client.put("realm-management", Arrays.asList("view-realm", "view-users", "manage-users"));
		composites.setClient(client);
		roleRepresentation.setComposites(composites);
		realmResource.roles().get(roleName).update(roleRepresentation);
		RoleRepresentation roleRepresentations = realmResource.roles().get(roleName).toRepresentation();
		return roleRepresentations;
	}

	private RealmResource getRealmResource() {
		Keycloak kc = KeycloakBuilder.builder().serverUrl(server_url).realm("master").username("Rubix")
				.password("Rubix").clientId("admin-cli")
				.resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();
		return kc.realm(realm);
	}

	private UsersResource getUsersResource() {
		RealmResource realmResource = getRealmResource();
		return realmResource.users();
	}

}
