package it.polito.ai.esercitazione3.controllers;

import it.polito.ai.esercitazione3.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getLogin(){
        return "login";
    }

}
