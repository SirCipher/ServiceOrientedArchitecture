package com.type2labs.nevernote.config;

import com.type2labs.nevernote.security.AuthenticationEntryPointImpl;
import com.type2labs.nevernote.security.AuthenticationRequestFilter;
import com.type2labs.nevernote.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Import({SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final AuthenticationEntryPointImpl unauthorizedHandler;

    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl, AuthenticationEntryPointImpl unauthorizedHandler) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.unauthorizedHandler = unauthorizedHandler;
    }

    /**
     * Returns the registered request filter
     *
     * @return the registered request filter
     */
    @Bean
    public AuthenticationRequestFilter authenticationRequestFilter() {
        return new AuthenticationRequestFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(userDetailsServiceImpl)
                .passwordEncoder(passwordEncoder());
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Returns the registered password encoder
     *
     * @return the registered password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the application's security for given routes
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // So we can access the H2 console @ localhost:8080/h2-console/
                .headers()
                .frameOptions()
                .disable()
                .and()
                // Enable CORS
                .cors()
                .and()
                .csrf()
                .disable()
                .exceptionHandling()
                // Set a custom exception handler
                .authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // Match any requests at the origin. For the client
                .antMatchers(
                        "/",
                        // Enable web sockets
                        "/ws/**")
                // Any request to /api/* must be authenticated that doesn't match other rules
                .permitAll()
                // Permit access to authorization url
                .antMatchers("/api/authorisation/**")
                .permitAll()
                // Only allow authorised requests
                .antMatchers("/api/**").authenticated();


        // Add our custom JWT security filter
        http.addFilterBefore(authenticationRequestFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}