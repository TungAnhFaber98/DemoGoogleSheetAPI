//package com.faber.googleapisheetproject;
//
//import org.springframework.cloud.security.oauth2.sso.EnableOAuth2Sso;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//
////@EnableOAuth2Sso
////@Configuration
//public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .csrf()
//                .disable()
//                .antMatcher("/**")
//                .authorizeRequests()
//                .antMatchers("/", "/welcome.html")
//                .permitAll()
//                .anyRequest()
//                .authenticated();
//    }
//}
