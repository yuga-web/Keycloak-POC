package com.tutorial.keycloakbackend.security;

import java.util.List;

import lombok.Data;

@Data
public class RealmRole {
	String name;
	String description;
	Boolean compositeRoles;
}
