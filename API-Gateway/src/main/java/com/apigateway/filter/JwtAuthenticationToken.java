/*package com.apigateway.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class JwtAuthenticationToken implements Authentication {

    /**
	 * 
	 */
	/*private static final long serialVersionUID = 1L;
	
	private final String token;
    private boolean authenticated;

    public JwtAuthenticationToken(String token) {
        this.token = token;
        this.authenticated = false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // No authorities needed for JWT
    }

    @Override
    public Object getCredentials() {
        return token; // The JWT token itself is the credential
    }

    @Override
    public Object getDetails() {
        return null; // No additional details
    }

    @Override
    public Object getPrincipal() {
        return token; // The token is the principal
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return null; // Not needed for JWT
    }
}*/
