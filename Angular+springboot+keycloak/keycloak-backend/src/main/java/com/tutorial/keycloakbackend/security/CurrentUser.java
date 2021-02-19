package com.tutorial.keycloakbackend.security;

import java.util.List;

import lombok.Data;

/**
 * @author Edward P. Legaspi | czetsuya@gmail.com
 * 
 * @version 0.0.1
 * @since 0.0.1
 */
@Data
public class CurrentUser {

    private String userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private List<String> roles;
}
