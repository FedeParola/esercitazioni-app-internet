package it.polito.ai.esercitazione3.controllers;

import it.polito.ai.esercitazione3.exceptions.BadRequestException;
import it.polito.ai.esercitazione3.exceptions.NotFoundException;
import it.polito.ai.esercitazione3.repositories.UserRepository;
import it.polito.ai.esercitazione3.security.jwt.JwtTokenProvider;
import it.polito.ai.esercitazione3.services.MailService;
import it.polito.ai.esercitazione3.services.UserService;
import it.polito.ai.esercitazione3.viewmodels.LoginDTO;
import it.polito.ai.esercitazione3.viewmodels.RecoverDTO;
import it.polito.ai.esercitazione3.viewmodels.RegistrationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
public class UserController {
    private static final Pattern pattern = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).{6,32}$");
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private MailService mailService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping(value = "/login", consumes= MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> login(@RequestBody @Valid LoginDTO loginDTO) throws BadRequestException {
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();

        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            List<String> roles = auth.getAuthorities().stream()
                                                      .map((GrantedAuthority a) -> a.getAuthority())
                                                      .collect(Collectors.toList());
            String token = jwtTokenProvider.createToken(email, roles);

            Map<String, String> response = new HashMap<>();
            response.put("token", token);

            return response;

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid email/password supplied");
        }
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void register(@RequestBody @Valid RegistrationDTO registrationDTO, HttpServletRequest request) throws BadRequestException {
        /* Check password confirmation */
        if (!registrationDTO.getPass().equals(registrationDTO.getConfPass())) {
            throw new BadRequestException("Password confirmation doesn't match");
        }

        String uuid = userService.registerUser(registrationDTO);

        /* Build the confirmation URL */
        String requestUrl = request.getRequestURL().toString();
        String confirmUrl = requestUrl.substring(0, requestUrl.lastIndexOf(request.getRequestURI())) + "/confirm/" + uuid;

        mailService.sendConfirmationMail(registrationDTO.getEmail(), confirmUrl);

        return;
    }

    @GetMapping(value = "/confirm/{randomUUID}")
    public void confirm(@PathVariable String randomUUID) throws NotFoundException {
        userService.confirmUser(randomUUID);

        return;
    }

    @PostMapping(value = "/recover", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void recover(@RequestBody Map<String, String> emailMap, HttpServletRequest request){

        String email = emailMap.get("email");
        String uuid = userService.createRecoverToken(email);
        if(uuid != null)
        {
            String requestUrl = request.getRequestURL().toString();
            String recoverUrl = requestUrl.substring(0, requestUrl.lastIndexOf(request.getRequestURI())) + "/recover/" + uuid;
            mailService.sendRecoverMail(email, recoverUrl);
        }

        return;
    }

    @GetMapping(value = "/recover/{randomUUID}")
    public String getPassChangeForm(@PathVariable String randomUUID, Model m){
        m.addAttribute("randomUUID", randomUUID);
        return "passChangeForm";
    }

    @PostMapping(value = "/recover/{randomUUID}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void postNewPass(@RequestBody @Valid RecoverDTO recoverDTO, @PathVariable String randomUUID) throws NotFoundException {
        String newPass = recoverDTO.getPass();
        Matcher m = pattern.matcher(newPass);
        if(newPass.equals(recoverDTO.getConfPass()) && m.matches())
        {
            userService.tryChangePassword(randomUUID, newPass);
        }
    }

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
