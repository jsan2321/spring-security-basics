package com.security04.controller;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class TestAuthController {

    @GetMapping("/get")
    public String helloGet(){
        return "Hello World - GET";
    }

    @GetMapping("/hello-secured")
    public String helloSecured(){
        return "Hello World - SECURED";
    }

    @PostMapping("/post")
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
