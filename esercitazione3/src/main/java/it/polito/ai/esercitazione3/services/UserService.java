package it.polito.ai.esercitazione3.services;

import it.polito.ai.esercitazione3.entities.ConfirmationToken;
import it.polito.ai.esercitazione3.entities.RecoverToken;
import it.polito.ai.esercitazione3.entities.User;
import it.polito.ai.esercitazione3.exceptions.BadRequestException;
import it.polito.ai.esercitazione3.exceptions.NotFoundException;
import it.polito.ai.esercitazione3.repositories.ConfirmationTokenRepository;
import it.polito.ai.esercitazione3.repositories.RecoverTokenRepository;
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
    private static final long RECOVER_TOKEN_EXPIRY_MIN = 30;
    private static final Logger log = LoggerFactory.getLogger(LineService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private RecoverTokenRepository recoverTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EntityManager entityManager;

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public String registerUser(RegistrationDTO registrationDTO) throws BadRequestException {
        /* Check duplicate user */
        User user = userRepository.findById(registrationDTO.getEmail()).orElse(null);
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
                    confirmationTokenRepository.delete(token);
                    userRepository.delete(user);
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

        /* Necessary, otherwise the system tries to persist the token before the user and returns an error */
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

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public void confirmUser(String uuid) throws NotFoundException {
        ConfirmationToken token = confirmationTokenRepository.findByUuid(uuid).orElseThrow(() -> new NotFoundException());

        User user = token.getUser();

        if (token.isExpired()) {
            /* Delete token and user */
            confirmationTokenRepository.delete(token);
            userRepository.delete(user);

            throw new NotFoundException();
        }

        /* Enable the user */
        user.setEnabled(true);
        userRepository.save(user);

        /* Delete the token */
        confirmationTokenRepository.delete(token);

        return;
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public void tryChangePassword(String UUID, String newPass) throws NotFoundException {
        RecoverToken token = recoverTokenRepository.findByUuid(UUID)
                .orElseThrow(()->new NotFoundException("Recover token with UUID " + UUID + " doesn't exist!"));

        if (token.isExpired())
        {
            recoverTokenRepository.delete(token);
            throw new NotFoundException("Recover token with UUID " + UUID + " has expired!");
        }
        else
        {
            User user = token.getUser();
            user.setPassword(newPass);
            userRepository.save(user);
            recoverTokenRepository.delete(token);
        }

        return;
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

        //PER DEBUG
        for(int i=1; i<5;i++) {
            roles = new ArrayList<>();
            roles.add("USER");
            user = User.builder()
                    .email("user" + i + "@email.it")
                    .enabled(true)
                    .roles(roles)
                    .password(passwordEncoder.encode("qwerty"))
                    .build();

            userRepository.save(user);
        }

        roles=new ArrayList<>();
        roles.add("USER");
        user = User.builder()
                .email("andpav@hotmail.it")
                .enabled(true)
                .roles(roles)
                .password(passwordEncoder.encode("qwerty"))
                .build();

        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findById(s).orElseThrow(() -> new UsernameNotFoundException("Email " + s + " not found"));

        /* Retrieve list of authorities from roles */
        List<SimpleGrantedAuthority> authList = user.getRoles().stream()
                                                               .map((r) -> new SimpleGrantedAuthority(r))
                                                               .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.isEnabled(),
                true, true, true, authList);
    }

    public String createRecoverToken(String email) {
        String uuid = UUID.randomUUID().toString();

        RecoverToken token = new RecoverToken();
        User u = userRepository.findById(email).orElse(null);

        if(u == null)
        {
             return null;
        }

        token.setUser(u);
        token.setUuid(uuid);
        token.setExpiryDate(new Timestamp(System.currentTimeMillis() + RECOVER_TOKEN_EXPIRY_MIN*60*1000));
        recoverTokenRepository.save(token);

        return uuid;
    }
}
