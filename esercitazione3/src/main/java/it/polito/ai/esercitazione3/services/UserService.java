package it.polito.ai.esercitazione3.services;

import it.polito.ai.esercitazione3.entities.ConfirmationToken;
import it.polito.ai.esercitazione3.entities.User;
import it.polito.ai.esercitazione3.exceptions.BadRequestException;
import it.polito.ai.esercitazione3.repositories.ConfirmationTokenRepository;
import it.polito.ai.esercitazione3.repositories.UserRepository;
import it.polito.ai.esercitazione3.viewmodels.RegistrationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class UserService implements InitializingBean, UserDetailsService {
    private static final long CONF_TOKEN_EXPIRY_HOURS = 24;
    private static final Logger log = LoggerFactory.getLogger(LineService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EntityManager entityManager;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String registerUser(RegistrationDTO registrationDTO) throws BadRequestException {
        /* Check duplicate user */
        User user = userRepository.findByEmail(registrationDTO.getEmail()).orElse(null);
        if (user != null) {
            if (user.isEnabled()) {
                throw new BadRequestException("Email " + registrationDTO.getEmail() + " already registered");

            /* Check if user isn't confirmed and confirmation is expired */
            } else {
                ConfirmationToken token = confirmationTokenRepository.findByUser(user).orElse(null);
                /* User disabled for other reason or waiting for confirmation */
                if (token == null || !token.isExpired()) {
                    throw new BadRequestException("Email " + registrationDTO.getEmail() + " already registered");

                /* Expired token, remove user and token and proceed with registration */
                } else {
                    userRepository.delete(user);
                    confirmationTokenRepository.delete(token);
                }
            }
        }

        /* Store the user in disabled state, with USER role */
        ArrayList<String> roles = new ArrayList<>();
        roles.add("USER");
        user = User.builder().email(registrationDTO.getEmail())
                .password(passwordEncoder.encode(registrationDTO.getPass()))
                .enabled(false)
                .roles(roles)
                .build();
        userRepository.save(user);

        /*Mandatory, otherwise the system tries to persist the token before the user and returns an error */
        entityManager.flush();

        /* Generate and store confirmation token */
        String uuid = UUID.randomUUID().toString(); // Random UUID
        ConfirmationToken token = new ConfirmationToken();
        token.setUser(user);
        token.setUuid(uuid);
        token.setExpiryDate(new Timestamp(System.currentTimeMillis() + CONF_TOKEN_EXPIRY_HOURS*60*60*1000));
        confirmationTokenRepository.save(token);

        return uuid;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        User user;
        ArrayList<String> roles;

        roles=new ArrayList<>();
        roles.add("SYSTEM-ADMIN");
        user = User.builder()
                .email("user0@email.it")
                .enabled(true)
                .roles(roles)
                .password(passwordEncoder.encode("qwerty"))
                .build();

        userRepository.save(user);

        for(int i=1; i<5;i++){
            roles= new ArrayList<>();
            roles.add("USER");
            user = User.builder()
                    .email("user"+i+"@email.it")
                    .enabled(true)
                    .roles(roles)
                    .password(passwordEncoder.encode("qwerty"))
                    .build();

            userRepository.save(user);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(s).orElseThrow(() -> new UsernameNotFoundException("Email " + s + " not found"));

        /* Retrieve list of authorities from roles */
        List<SimpleGrantedAuthority> authList = user.getRoles().stream()
                                                               .map((r) -> new SimpleGrantedAuthority(r))
                                                               .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.isEnabled(),
                true, true, true, authList);
    }
}
