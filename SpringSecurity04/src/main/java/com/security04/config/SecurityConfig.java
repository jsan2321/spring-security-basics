package com.security04.config;

import com.security04.config.filter.JwtTokenValidator;
import com.security04.service.UserDetailServiceImpl;
import com.security04.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtils jwtUtils;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(http -> {
                    // Configuring public endpoints
                    http.requestMatchers(HttpMethod.POST, "/auth/**").permitAll();

                    // Configuring private endpoints
                    http.requestMatchers(HttpMethod.GET, "/method/get").hasAuthority("READ");
                    http.requestMatchers(HttpMethod.POST, "/method/post").hasAuthority("CREATE");
                    http.requestMatchers(HttpMethod.DELETE, "/method/delete").hasAuthority("DELETE");
                    http.requestMatchers(HttpMethod.PUT, "/method/put").hasAuthority("UPDATE");
                    http.requestMatchers(HttpMethod.POST, "/method/post").hasAnyRole("ADMIN", "DEVELOPER");
                    http.requestMatchers(HttpMethod.GET, "/method/get").hasAnyRole("INVITED");
                    //http.requestMatchers(HttpMethod.GET, "/method/get").hasAnyAuthority("INVITED"); // INVITED is a roles not authority



                    // Configure the rest of the endpoints - NOT SPECIFIED
                    http.anyRequest().denyAll();
                })
                .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class) // token needs to pass through this filter (token validations) before (because of) BasicAuthenticationFilter
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailServiceImpl userDetailService){ // have userDetailService fetch users in the database
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailService);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(); // encrypt passwords and check if they are valid
    }

}
