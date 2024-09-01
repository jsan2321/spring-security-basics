package com.security02;

import com.security02.models.ERole;
import com.security02.models.RoleEntity;
import com.security02.models.UserEntity;
import com.security02.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class SpringSecurity02Application {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurity02Application.class, args);
    }

    // create user once the application is started
    @Bean
    CommandLineRunner init() {
        return args -> {
            UserEntity userEntity = UserEntity.builder()
                    .email("santi@email.com")
                    .username("santi")
                    .password(passwordEncoder.encode("1234"))
                    .roles(Set.of(RoleEntity.builder()
                            .name(ERole.valueOf(ERole.ADMIN.name()))
                            .build()))
                    .build();

            UserEntity userEntity2 = UserEntity.builder()
                    .email("migue@email.com")
                    .username("migue")
                    .password(passwordEncoder.encode("1234"))
                    .roles(Set.of(RoleEntity.builder()
                            .name(ERole.valueOf(ERole.USER.name()))
                            .build()))
                    .build();

            UserEntity userEntity3 = UserEntity.builder()
                    .email("joel@email.com")
                    .username("joel")
                    .password(passwordEncoder.encode("1234"))
                    .roles(Set.of(RoleEntity.builder()
                            .name(ERole.valueOf(ERole.INVITED.name()))
                            .build()))
                    .build();

            userRepository.save(userEntity);
            userRepository.save(userEntity2);
            userRepository.save(userEntity3);
        };
    }

}
