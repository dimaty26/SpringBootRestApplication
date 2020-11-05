package com.zmeevsky.springmvc.config;

import com.zmeevsky.springmvc.handler.LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier(value = "userDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/show-login-page").permitAll()
                .and()
                .authorizeRequests()
                .antMatchers("/users/user").access("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
                .and()
                .authorizeRequests()
                .antMatchers("/users/admin").access("hasRole('ROLE_ADMIN')")
                .and()
                .formLogin()
                    .loginPage("/show-login-page")
                    .successHandler(myAuthenticationSuccessHandler())
                    .permitAll()
                .and()
                .logout()
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/show-login-page?logout")
                .permitAll();

        http.userDetailsService(userDetailsService());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/resources/**", "/static/**", "/js/**", "/css/**", "/fonts/**");
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationSuccessHandler myAuthenticationSuccessHandler() {
        return new LoginSuccessHandler();
    }
}
