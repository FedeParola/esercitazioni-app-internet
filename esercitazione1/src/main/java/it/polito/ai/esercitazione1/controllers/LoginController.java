package it.polito.ai.esercitazione1.controllers;

import it.polito.ai.esercitazione1.User;
import it.polito.ai.esercitazione1.ViewModels.LoginDTO;
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
public class LoginController {
    @Autowired
    private ConcurrentMap<String, User> usersMap;

    @GetMapping("/login")
    public String getLogin(){
        return "login";
    }

    @PostMapping("/login")
    public String postLogin(@Valid LoginDTO lDTO, BindingResult res, Model m){

        if(res.hasErrors()){
            m.addAttribute("errLogin", "Wrong request format");
            return "login";
        }

        User u = usersMap.get(lDTO.getEmail());
        if(u == null || !u.getPass().equals(lDTO.getPass())){
            m.addAttribute("errLogin", "Wrong email or password");
            m.addAttribute("email", lDTO.getEmail());
            return "login";
        }

        m.addAttribute("msg", "User logged in");
        return "private";
    }
}
