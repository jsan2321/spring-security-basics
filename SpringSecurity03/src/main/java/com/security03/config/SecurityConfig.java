package com.security03.config;

import com.security03.service.UserDetailServiceImpl;
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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception { // httpSecurity goes through all the filters... SecurityFilterChain is validated because of DelegatingFilterProxy
        return httpSecurity
                .csrf(csrf -> csrf.disable()) // it is necessary when using MVC because of forms
                //.csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults()) // works with username and password, not token
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // STATELESS is ideal when using web apps
                .authorizeHttpRequests(http -> { // Configure permissions to endpoints
                    // Configuring public endpoints
                    http.requestMatchers(HttpMethod.GET, "/auth/get").permitAll(); // allows access to everyone, without restriction
                    //http.requestMatchers(HttpMethod.GET, "/auth/hello-secured").hasAuthority("READ"); // allows access to everyone, without restriction

                    // Configuring private endpoints
                    http.requestMatchers(HttpMethod.POST, "/auth/post").hasAnyRole("ADMIN", "DEVELOPER");
                    http.requestMatchers(HttpMethod.PATCH, "/auth/patch").hasAnyAuthority("REFACTOR");

                    // Configure the rest of the endpoints - NOT SPECIFIED
                    http.anyRequest().denyAll(); // even with correct credentials, access is denied
                    // http.anyRequest().authenticated(); // if authenticated, access is granted
                })
                .build(); // httpSecurity.build() throws an exception and works with the Builder pattern
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        return httpSecurity
//                .csrf(csrf -> csrf.disable())
//                .httpBasic(Customizer.withDefaults())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .build();
//    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailServiceImpl userDetailService){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailService);
        return provider;
    }

//    @Bean
//    public UserDetailsService userDetailsService() { // imagine this (in-memory) users come from a database...
//        //UserDetails userDetails =
//        List<UserDetails> userDetailsList = new ArrayList<>();
//
//        userDetailsList.add(User.withUsername("mints")
//                .password("1234")
//                .roles("ADMIN")
//                .authorities("READ", "CRATE")
//                .build());
//
//        userDetailsList.add(User.withUsername("danil")
//                .password("1234")
//                .roles("ADMIN")
//                .authorities("READ")
//                .build());
//
//        return new InMemoryUserDetailsManager(userDetailsList); // load users in memory
//    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
