package com.security02.controller;

import com.security02.controller.request.CreateUserDTO;
import com.security02.models.ERole;
import com.security02.models.RoleEntity;
import com.security02.models.UserEntity;
import com.security02.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class PrincipalController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/hello")
    public String hello() {
        return "Hello World Not Secured";
    }

    @GetMapping("/helloSecured")
    public String helloSecured() {
        return "Hello World Secured";
    }

    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {

        Set<RoleEntity> roles = createUserDTO.getRoles().stream()
                .map(role -> RoleEntity.builder()
                        .name(ERole.valueOf(role))
                        .build())
                .collect(Collectors.toSet()); // converting from string to given object

        UserEntity userEntity = UserEntity.builder()
                .username(createUserDTO.getUsername())
                //.password(createUserDTO.getPassword())
                .password(passwordEncoder.encode(createUserDTO.getPassword())) // password is encrypted (go to db and check it)
                .email(createUserDTO.getEmail())
                .roles(roles)
                .build();

        userRepository.save(userEntity);

        return ResponseEntity.ok(userEntity);

    }

    @DeleteMapping("/deleteUser") // with persistence, role can be available even when the user is deleted
    public String deleteUser(@RequestParam String id) {
        userRepository.deleteById(Long.parseLong(id));
        return "Deleted user with id".concat(id);
    }

}
