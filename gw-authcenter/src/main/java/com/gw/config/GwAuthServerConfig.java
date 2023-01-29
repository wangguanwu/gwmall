package com.gw.config;

import com.gw.component.GwTokenEnhancer;
import com.gw.properties.JwtCAProperties;
import com.gw.mall.service.GwUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.util.Arrays;

/**
 * AS服务器的配置
 */
@Configuration
@EnableAuthorizationServer
@EnableConfigurationProperties(value = JwtCAProperties.class)
public class GwAuthServerConfig extends AuthorizationServerConfigurerAdapter {


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private GwUserDetailService gwUserDetailService;

    @Autowired
    private JwtCAProperties jwtCAProperties;


    /**
     * 方法实现说明:颁发的token通过jwt存储

     */
    @Bean
    public TokenStore tokenStore(){
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        //jwt的密钥
        converter.setKeyPair(keyPair());
        return converter;
    }

    @Bean
    public KeyPair keyPair() {
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource(jwtCAProperties.getKeyPairName()), jwtCAProperties.getKeyPairSecret().toCharArray());
        return keyStoreKeyFactory.getKeyPair(jwtCAProperties.getKeyPairAlias(), jwtCAProperties.getKeyPairStoreSecret().toCharArray());
    }


    @Bean
    public GwTokenEnhancer gwTokenEnhancer() {
        return new GwTokenEnhancer();
    }



    /**
     * 方法实现说明:认证服务器能够给哪些 客户端颁发token  我们需要把客户端的配置 存储到
     * 数据库中 可以基于内存存储和db存储
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetails());
    }

    /**
     * 方法实现说明:用于查找我们第三方客户端的组件 主要用于查找 数据库表 oauth_client_details
     */
    @Bean
    public ClientDetailsService clientDetails() {
        return new JdbcClientDetailsService(dataSource);
    }

    /**
     * 方法实现说明:授权服务器的配置的配置
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(gwTokenEnhancer(),jwtAccessTokenConverter()));

        endpoints.tokenStore(tokenStore()) //授权服务器颁发的token 怎么存储的
                .tokenEnhancer(tokenEnhancerChain)
                .userDetailsService(gwUserDetailService) //用户来获取token的时候需要 进行账号密码
                .authenticationManager(authenticationManager);
    }


    /**
     * 方法实现说明:授权服务器安全配置
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //第三方客户端校验token需要带入 clientId 和clientSecret来校验
        security .checkTokenAccess("isAuthenticated()")
                 .tokenKeyAccess("isAuthenticated()");//来获取我们的tokenKey需要带入clientId,clientSecret

        security.allowFormAuthenticationForClients();

    }

}
