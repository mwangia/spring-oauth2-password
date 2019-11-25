package com.michaelw.tutorials.springbootoauth2.auth;

import com.michaelw.tutorials.springbootoauth2.enums.UserRole;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author mwangia on 11/20/19.
 */
@Configuration
@EnableAuthorizationServer
public class Oauth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    private static final String OAUTH_GRANT_TYPE = "password";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String MOBILE_CLIENT = "mobile_client";
    //intentionally empty. we don't need a client secret since we're using the password grant
    private static final String MOBILE_CLIENT_SECRET = "";
    private static final String[] SCOPES = {"read", "write", "trust"};
    private static final Logger log = LoggerFactory.getLogger(Oauth2AuthorizationServerConfig.class);

    private AuthenticationManager authenticationManager;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${keystore.alias}")
    private String keystoreAlias;

    @Value("${keystore.password}")
    private String keystorePassword;

    @Value("${publickey.filename}")
    private String publicKeyFilename;

    @Value("${keystore.filename}")
    private String keystoreFilename;

    Oauth2AuthorizationServerConfig(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        this.authenticationManager = authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource(keystoreFilename),
                keystorePassword.toCharArray());
        jwtAccessTokenConverter.setKeyPair(keyStoreKeyFactory.getKeyPair(keystoreAlias));
        Resource resource = new ClassPathResource(publicKeyFilename);
        try (InputStream inputStream = resource.getInputStream()) {
            String publicKey = IOUtils.toString(inputStream, Charset.forName("UTF-8"));
            jwtAccessTokenConverter.setVerifierKey(publicKey);
        } catch (IOException exc) {
            log.error("Error fetching public key", exc);
        }
        return jwtAccessTokenConverter;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory().withClient(MOBILE_CLIENT)
                .secret(bCryptPasswordEncoder.encode(MOBILE_CLIENT_SECRET))
                .authorizedGrantTypes(OAUTH_GRANT_TYPE, REFRESH_TOKEN)
                .authorities(UserRole.STANDARD.name(), UserRole.ADMIN.name(), UserRole.SUPER_ADMIN.name())
                .scopes(SCOPES);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.authenticationManager(authenticationManager)
                .tokenStore(tokenStore())
                .accessTokenConverter(accessTokenConverter());
    }
}
