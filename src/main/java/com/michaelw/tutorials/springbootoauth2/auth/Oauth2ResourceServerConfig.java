package com.michaelw.tutorials.springbootoauth2.auth;

import com.michaelw.tutorials.springbootoauth2.enums.UserRole;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;


/**
 * @author mwangia on 11/20/19.
 */
@Configuration
@EnableResourceServer
public class Oauth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(authorizedRequests -> authorizedRequests.antMatchers("/greetings/**")
                .hasAuthority(UserRole.STANDARD.name()));
    }

}
