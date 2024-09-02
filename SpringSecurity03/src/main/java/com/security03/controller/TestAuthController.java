package com.security03.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
//@PreAuthorize("denyAll()") // it denies access by default
public class TestAuthController {

    @GetMapping("/get")
    //@PreAuthorize("permitAll()")
    public String helloGet(){
        return "Hello World - GET";
    }

    @GetMapping("/hello-secured")
    //@PreAuthorize("hasAuthority('READ')")
    public String helloSecured(){
        return "Hello World - SECURED";
    }

    @PostMapping("/post")
    //@PreAuthorize("hasAuthority('READ') or hasAuthority('CREATE')")
    public String helloPost(){
        return "Hello World - POST";
    }

    @PutMapping("/put")
    public String helloPut(){
        return "Hello World - PUT";
    }

    @DeleteMapping("/delete")
    public String helloDelete(){
        return "Hello World - DELETE";
    }

    @PatchMapping("/patch")
    public String helloPatch(){
        return "Hello World - PATCH";
    }

}
