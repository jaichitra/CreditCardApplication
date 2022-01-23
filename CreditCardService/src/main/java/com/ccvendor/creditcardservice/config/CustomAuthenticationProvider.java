package com.ccvendor.creditcardservice.config;

import com.ccvendor.creditcardservice.data.AppUserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    @Autowired
    private FilePasswordManager passwordManager;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final String uName = authentication.getName();

        final AppUserCredentials appUserCredentialsObject = this.passwordManager.getCredentials(uName);

        if (appUserCredentialsObject != null & appUserCredentialsObject.getPassword().equals(authentication.getCredentials().toString())) {
            log.info("User authenticated : " + uName);
            final List<GrantedAuthority> authorities = new ArrayList<>();

            if (appUserCredentialsObject.getRoles() != null && appUserCredentialsObject.getRoles().size() > 0)
                appUserCredentialsObject.getRoles().forEach(t -> authorities.add(new SimpleGrantedAuthority(t)));

            return new UsernamePasswordAuthenticationToken(uName,
                    authentication.getCredentials().toString(), authorities);
        }
        log.info("Authentication failed for User : " + uName);
        return null;
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
