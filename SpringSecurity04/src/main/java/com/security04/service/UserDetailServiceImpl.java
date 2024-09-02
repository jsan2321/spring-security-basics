package com.security04.service;

import com.security04.controller.dto.AuthCreateUserRequest;
import com.security04.controller.dto.AuthLoginRequest;
import com.security04.controller.dto.AuthResponse;
import com.security04.persistence.entity.RoleEntity;
import com.security04.persistence.entity.UserEntity;
import com.security04.persistence.repository.RoleRepository;
import com.security04.persistence.repository.UserRepository;
import com.security04.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {

        UserEntity userEntity = userRepository.findUserEntityByUsername(username).orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe."));

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        userEntity.getRoles().forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        userEntity.getRoles().stream().flatMap(role -> role.getPermissionList().stream()).forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));


        return new User(userEntity.getUsername(), userEntity.getPassword(), userEntity.isEnabled(), userEntity.isAccountNoExpired(), userEntity.isCredentialNoExpired(), userEntity.isAccountNoLocked(), authorityList);
    }

    public AuthResponse createUser(AuthCreateUserRequest createRoleRequest) { //

        String username = createRoleRequest.username();
        String password = createRoleRequest.password();
        List<String> rolesRequest = createRoleRequest.roleRequest().roleListName(); // get list of roles

        Set<RoleEntity> roleEntityList = roleRepository.findRoleEntitiesByRoleEnumIn(rolesRequest).stream().collect(Collectors.toSet()); // roles from database that meet client requests
        //Set<RoleEntity> roleEntityList = new HashSet<>(roleRepository.findRoleEntitiesByRoleEnumIn(rolesRequest));

        if (roleEntityList.isEmpty()) {
            throw new IllegalArgumentException("The roles specified does not exist.");
        }

        // building the user
        UserEntity userEntity = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles(roleEntityList)
                .isEnabled(true)
                .accountNoLocked(true)
                .accountNoExpired(true)
                .credentialNoExpired(true)
                .build();

        UserEntity userSaved = userRepository.save(userEntity); // user to generate the token with

        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>(); // list of permissions

        userSaved.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name())))); // adding roles to the list

        userSaved.getRoles()
                .stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName()))); // each role has a list of permissions... adding those permissions to the list

        SecurityContext securityContextHolder = SecurityContextHolder.getContext();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userSaved, null, authorities);
        //Authentication authentication = new UsernamePasswordAuthenticationToken(userSaved.getUsername(), userSaved.getPassword(), authorities);

        String accessToken = jwtUtils.createToken(authentication);

        AuthResponse authResponse = new AuthResponse(username, "User created successfully", accessToken, true);
        //AuthResponse authResponse = new AuthResponse(userSaved.getUsername(), "User created successfully", accessToken, true);
        return authResponse;
    }

    public AuthResponse loginUser(AuthLoginRequest authLoginRequest) { // generate access token

        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.createToken(authentication);
        AuthResponse authResponse = new AuthResponse(username, "User loged succesfully", accessToken, true);
        return authResponse;
    }

    public Authentication authenticate(String username, String password) { // check valid credentials
        UserDetails userDetails = this.loadUserByUsername(username); // check if user exist in database

        if (userDetails == null) {
            throw new BadCredentialsException(String.format("Invalid username or password"));
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Incorrect Password");
        }

        return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities()); // if user and password are valid
        //return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }
}