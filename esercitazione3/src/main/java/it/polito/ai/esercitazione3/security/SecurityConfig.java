package it.polito.ai.esercitazione3.security;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        //configura gli utenti e i ruoli
    }

    @Override
    protected void configure(HttpSecurity http)
            throws Exception {
        //configura le URL da proteggere
    }


}
