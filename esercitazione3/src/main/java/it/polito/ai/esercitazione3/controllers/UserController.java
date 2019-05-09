package it.polito.ai.esercitazione3.controllers;

import it.polito.ai.esercitazione3.repositories.UserRepository;
import it.polito.ai.esercitazione3.security.jwt.JwtTokenProvider;
import it.polito.ai.esercitazione3.services.MailService;
import it.polito.ai.esercitazione3.services.UserService;
import it.polito.ai.esercitazione3.viewmodels.LoginDTO;
import it.polito.ai.esercitazione3.viewmodels.RegistrationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.AuthenticationException;

import javax.print.attribute.standard.Media;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class UserController {
    @Autowired
    private MailService mailService;
    @Autowired
    private UserRepository userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserRepository userRepository;

    @PostMapping(value = "/login", consumes= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login(@RequestBody @Valid LoginDTO loginDTO, BindingResult bindingResult){
        try {
            String email= loginDTO.getEmail();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, loginDTO.getPass()));
            String token = jwtTokenProvider.createToken(email, this.userRepository.findByUsername(email).orElseThrow(() ->
                    new UsernameNotFoundException("Email " + email + "not found")).getRoles());
            return ok(token);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid email/password supplied");
        }
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void register(@RequestBody @Valid RegistrationDTO registrationDTO){
//        if(!userService.exists(registrationDTO.getEmail()))
//        {
//            if(registrationDTO.getPass().equals(registrationDTO.getConfPass()))
//            {
//                if(true /*controlla che le password siano sufficientemente sicure...*/)
//                {
//                    /*ma lo registra in condizione di attesa di verifica*/
//                    userService.register(registrationDTO);
//                    mailService.sendActivationMail(registrationDTO.getEmail());
//                }
//            }
//        }
        return;
    }

    @GetMapping(value = "/confirm/{randomUUID}")
    public void confirm(@PathVariable String randomUUID){
//        if(true /*randomUUID corrisponde a un utente in corso di verifica e questa non è ancora scaduta*/)
//        {
//            userService.activate(/*passo la mail associata al randomUUID*/);
//            /*restituisci 200 OK*/
//        }
//        else
//        {
//            /*restituisci 404 NOT FOUND*/
//        }
        return;
    }

    @PostMapping(value = "/recover", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void recover(@RequestBody String mail){
//        if(userService.exists(mail))
//        {
//            mailService.sendRecoverMail(mail);
//        }
        return /*200 OK in ogni caso*/;
    }

    @GetMapping(value = "/recover/{randomUUID}")
    public String getPassChangeForm(@PathVariable String randomUUID){
        /*creare una pagina html per il cambio della pass nel package resources*/
        return "passChangeForm";
    }

    @PostMapping(value = "/recover/{randomUUID}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void postNewPass(){
        return;
    }

    /*Tutti gli end-point precedentemente esistenti dovranno essere configurati per essere accessibili solo se viene
    passato, come intestazione della richiesta, il campo “Authorization: bearer <JWT>” e che il JWT sia valido e non
    scaduto. In caso contrario risponderanno 401 – Unauthorized. Se dal JWT risulta che l’utente corrente ha un ruolo
     non consono con la richiesta in corso, risponderà 403 – Forbidden.*/

    /*queste URL devono essere accessibili solo al system-admin o all'admin di una linea*/
    @GetMapping(value = "/users")
    public void getUsers(){
        return;
    }

    @PutMapping(value = "/users/{userID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void modifyUserRole(){
        return;
    }

}
