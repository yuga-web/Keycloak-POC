package com.czetsuya.web.application;

import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.czetsuya.keycloak.service.KeycloakAdminClientService;
import com.czetsuya.security.CurrentUser;

/**
 * We will be using
 * https://www.keycloak.org/docs-api/9.0/javadocs/org/keycloak/admin/client/resource/RolesResource.html
 * API.
 * 
 * @author Edward P. Legaspi | czetsuya@gmail.com
 * 
 * @version 0.0.1
 * @since 0.0.1
 */
@RestController
@RequestMapping(path = "/keycloak", produces = MediaType.APPLICATION_JSON_VALUE)
public class KeycloakController {
	@Autowired
    private RestTemplate restTemplate;
	
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
    @GetMapping("/users")
    public Object AllUser() {
        return keycloakAdminClientService.getAllUser();
    }
    @PostMapping("/addusers")
    public Object createUser( @RequestBody CurrentUser user ) {
        return keycloakAdminClientService.createUser(user);
    }
    
}
