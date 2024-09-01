package com.security02.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRolesController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/accessAdmin")
    public String accessAdmin() {
        return "Hello, you were able to access with ADMIN role";
    }


    //@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    //@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/accessUser")
    public String accessUser() {
        return "Hello, you were able to access with USER role";
    }

    @PreAuthorize("hasRole('INVITED')")
    @GetMapping("/accessInvited")
    public String accessInvited() {
        return "Hello, you were able to access with INVITED role";
    }

}
