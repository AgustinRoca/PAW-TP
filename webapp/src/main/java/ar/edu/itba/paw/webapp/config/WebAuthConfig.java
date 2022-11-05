package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
@ComponentScan(basePackages = {"ar.edu.itba.paw.webapp.auth"})
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class WebAuthConfig extends WebSecurityConfigurerAdapter {
    private static final String API_PATH = "/api";

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JWTAuthenticationFilter authenticationFilter = new JWTAuthenticationFilter();
        authenticationFilter.setAuthenticationManager(this.authenticationManagerBean());
        return authenticationFilter;
    }

    @Bean
    public JWTAuthorizationFilter jwtAuthorizationFilter() throws Exception {
        return new JWTAuthorizationFilter(this.authenticationManager());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(this.userDetailsService)
                .passwordEncoder(this.passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher(API_PATH + "/**").sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().authorizeRequests()
                .antMatchers(HttpMethod.POST, API_PATH + "/login").anonymous()
                .antMatchers(HttpMethod.POST, API_PATH + "/auth/logout").authenticated()
                .antMatchers(HttpMethod.GET, API_PATH + "/verify/**").permitAll() // Verifies a user
                .antMatchers(HttpMethod.POST, API_PATH + "/auth/refresh").permitAll() // Refreshes the access token
                .antMatchers(HttpMethod.POST, API_PATH + "/users").anonymous() // Creates a user
                .antMatchers(HttpMethod.PUT, API_PATH + "/users/**").authenticated()
                .antMatchers(HttpMethod.GET, API_PATH + "/users/**").permitAll() // Creates a user
                .antMatchers(HttpMethod.GET, API_PATH + "/specialties").permitAll()
                .antMatchers(HttpMethod.GET, API_PATH + "/countries/**").permitAll()
                .antMatchers(HttpMethod.GET, API_PATH + "/provinces/**").permitAll()
                .antMatchers(HttpMethod.GET, API_PATH + "/localities/**").permitAll()
                .antMatchers(HttpMethod.GET, API_PATH + "/workdays/**").permitAll()
                .antMatchers(HttpMethod.POST, API_PATH + "/workdays/**").hasRole(UserRole.DOCTOR.toString())
                .antMatchers(HttpMethod.GET, API_PATH + "/doctors/**").permitAll()
                .antMatchers(HttpMethod.PUT, API_PATH + "/doctors/**").hasRole(UserRole.DOCTOR.toString())
                .antMatchers(API_PATH + "/**").authenticated()
            .and()
                .addFilter(this.jwtAuthorizationFilter())  // Verifies JWT if provided
                .addFilterAfter(this.jwtAuthenticationFilter(), JWTAuthorizationFilter.class) // Authenticates a user and sends JWT and Refresh token
                .exceptionHandling()
                .authenticationEntryPoint(new JWTAuthenticationEntryPoint()) // Handles forbidden/unauthorized page access exceptions
                .accessDeniedHandler(new AccessDeniedHandlerImpl()) // Handles role exceptions
            .and().csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/js/**", "/css/**", "/img/**");
    }
}
