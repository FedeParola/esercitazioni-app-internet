package it.polito.ai.esercitazione1.controllers;

import it.polito.ai.esercitazione1.ErrorMessageBuilder;
import it.polito.ai.esercitazione1.User;
import it.polito.ai.esercitazione1.ViewModels.RegistrationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

@Controller
public class RegisterController {
    @Autowired
    private ConcurrentMap<String, User> usersMap;

    @GetMapping("/register")
    public String getRegister(){
        return "register";
    }

    @PostMapping("/register")
    public String postRegister(@Valid RegistrationDTO rDTO, BindingResult res, Model m){
        if(res.hasErrors() || !rDTO.getPass().equals(rDTO.getConfPass()) || usersMap.containsKey(rDTO.getEmail())){

            if(res.getFieldErrors("name").size() > 0){
                m.addAttribute("errName", ErrorMessageBuilder.build(res.getFieldErrors("name")));
            }

            if(res.getFieldErrors("surname").size() > 0){
                m.addAttribute("errSurname", ErrorMessageBuilder.build(res.getFieldErrors("surname")));
            }

            if(res.getFieldErrors("email").size() > 0){
                m.addAttribute("errEmail", ErrorMessageBuilder.build(res.getFieldErrors("email")));
            }

            if(res.getFieldErrors("pass").size() > 0){
                m.addAttribute("errPass", ErrorMessageBuilder.build(res.getFieldErrors("pass")));
            }

            if(res.getFieldErrors("privacy").size() > 0){
                m.addAttribute("errPrivacy", "Privacy policy must be accepted");
            }

            if(rDTO.getEmail()!=null && usersMap.containsKey(rDTO.getEmail())){
                m.addAttribute("errExistingUser", "User already registered");
            }

            if(rDTO.getPass()!=null && !rDTO.getPass().equals(rDTO.getConfPass())){
                m.addAttribute("errConfPass", "Not corresponding passwords");
            }

            m.addAttribute("name", rDTO.getName());
            m.addAttribute("surname", rDTO.getSurname());
            m.addAttribute("email", rDTO.getEmail());
            return "register";

        }else{
            User u = User.builder()
                    .email(rDTO.getEmail())
                    .name(rDTO.getName())
                    .surname(rDTO.getSurname())
                    .pass(rDTO.getPass())
                    .build();


            if(usersMap.putIfAbsent(rDTO.getEmail(), u) != null){
                m.addAttribute("errExistingUser", "User already registered");

                m.addAttribute("name", rDTO.getName());
                m.addAttribute("surname", rDTO.getSurname());
                m.addAttribute("email", rDTO.getEmail());

                return "register";
            }
        }

        m.addAttribute("msg", "User registered");
        return "private";
    }
}