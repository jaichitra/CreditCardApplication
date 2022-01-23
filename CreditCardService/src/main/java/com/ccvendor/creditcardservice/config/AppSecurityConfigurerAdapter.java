package com.ccvendor.creditcardservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        jsr250Enabled = true
)
public class AppSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    private static final Logger log = LoggerFactory.getLogger(AppSecurityConfigurerAdapter.class.getName());

    @Autowired
    private RestBasicAuthenticationEntryPoint entryPoint;

    @Autowired
    private AuthenticationProvider authProvider;

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder builder) throws Exception {
        log.info("Global Configuration with Authentication Provider Initialized, authProvider Instance : "
                + this.authProvider.toString());
        builder.authenticationProvider(this.authProvider);
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and().anonymous().disable();
        http.httpBasic()
                .authenticationEntryPoint(this.entryPoint);

        log.info("Http Security configured");
    }
}