package com.michaelw.tutorials.springbootoauth2.auth;

import com.michaelw.tutorials.springbootoauth2.enums.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * @author mwangia on 11/20/19.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        BCryptPasswordEncoder bCryptPasswordEncoder = bCryptPasswordEncoder();
        //this is only for demo purposes. In prod these credentials  should be secured accordingly e.g in a vault
        UserDetails userDetails = User.withUsername("mike")
                .password(bCryptPasswordEncoder.encode("mypassword"))
                .authorities(UserRole.STANDARD.name())
                .build();
        InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager(userDetails);
        auth.userDetailsService(userDetailsManager).passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    //expose AuthenticationManager as a bean
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
