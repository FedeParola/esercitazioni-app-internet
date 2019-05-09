package it.polito.ai.esercitazione3.security;

import it.polito.ai.esercitazione3.security.jwt.JwtConfigurer;
import it.polito.ai.esercitazione3.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        //configura gli utenti e i ruoli
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .authorizeRequests()
                    .antMatchers("/login").permitAll()
                    .antMatchers("/register").permitAll()
                    .antMatchers("/confirm/*").permitAll()
                    .antMatchers("/recover/*").permitAll()
                    .antMatchers("/users/*").hasRole("")
                    .anyRequest().authenticated()
        .and().logout()
        .and().exceptionHandling()
                .authenticationEntryPoint(entryPoint())
        .and().apply(new JwtConfigurer(jwtTokenProvider));

    }

    @Bean
    public AuthenticationEntryPoint entryPoint(){
        return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
    }


}
