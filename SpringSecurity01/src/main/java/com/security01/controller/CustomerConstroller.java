package com.security01.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("v1")
public class CustomerConstroller {

    @Autowired
    private SessionRegistry sessionRegistry;

    @GetMapping("/index")
    public String index() {
        return "Hello World!";
    }

    @GetMapping("/index2")
    public String index2() {
        return "Hello World Not SECURED";
    }

    @GetMapping("/session")
    public ResponseEntity<?> getDetailsSession() {
        String sessionId = "";
        User userObject = null;

        List<Object> sessions = sessionRegistry.getAllPrincipals();

        for (Object session : sessions) {
            if (session instanceof User) {
                userObject = (User) session; // get authenticated user information
            }

            List<SessionInformation> sessionInformations = sessionRegistry.getAllSessions(session, false); // get all sessions

            for (SessionInformation sessionInformation : sessionInformations) {
                sessionId = sessionInformation.getSessionId(); // get sessions IDs
            }
        }

        // RestController uses jackson library to serialize the Map into a jackson to show in the browser
        Map<String, Object> reponse = new HashMap<>();
        reponse.put("response", "Hello World");
        reponse.put("sessionId", sessionId);
        reponse.put("sessionUser", userObject);

        return ResponseEntity.ok(reponse);
    }

    /*
    Username: user (by default)
    Password: (found in the logs)

    Type "http://localhost:8080/logout" to be signed out (from session)
    */
}
