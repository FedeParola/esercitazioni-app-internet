package it.polito.ai.esercitazione3.services;

import it.polito.ai.esercitazione3.entities.ConfirmationToken;
import it.polito.ai.esercitazione3.entities.Line;
import it.polito.ai.esercitazione3.entities.RecoverToken;
import it.polito.ai.esercitazione3.entities.User;
import it.polito.ai.esercitazione3.exceptions.BadRequestException;
import it.polito.ai.esercitazione3.exceptions.ForbiddenException;
import it.polito.ai.esercitazione3.exceptions.NotFoundException;
import it.polito.ai.esercitazione3.repositories.ConfirmationTokenRepository;
import it.polito.ai.esercitazione3.repositories.LineRepository;
import it.polito.ai.esercitazione3.repositories.RecoverTokenRepository;
import it.polito.ai.esercitazione3.repositories.UserRepository;
import it.polito.ai.esercitazione3.security.AuthorizationManager;
import it.polito.ai.esercitazione3.viewmodels.AuthorizationDTO;
import it.polito.ai.esercitazione3.viewmodels.RegistrationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.GrantedAuthority;
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
import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserService implements InitializingBean, UserDetailsService {
    private static final long CONF_TOKEN_EXPIRY_HOURS = 24;
    private static final long RECOVER_TOKEN_EXPIRY_MIN = 30;
    private static final Logger log = LoggerFactory.getLogger(LineService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LineRepository lineRepository;
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

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public void tryChangePassword(String UUID, String newPass) throws NotFoundException {
        RecoverToken token = recoverTokenRepository.findByUuid(UUID)
                .orElseThrow(()->new NotFoundException("Recover token with UUID " + UUID + " doesn't exist!"));

        if (token.isExpired()) {
            recoverTokenRepository.delete(token);
            throw new NotFoundException("Recover token with UUID " + UUID + " has expired!");

        } else {
            User user = token.getUser();
            user.setPassword(passwordEncoder.encode(newPass));
            userRepository.save(user);
            recoverTokenRepository.delete(token);
        }

        return;
    }

    public List<String> getAllUsers(Optional<Integer> page, Optional<Integer> size) throws BadRequestException {
        List<String> users = new ArrayList<>();

        if(page.isPresent() && size.isPresent()){
            for (User u: userRepository.findAll(PageRequest.of(page.get(), size.get()))) {
                users.add(u.getEmail());
            }
        }else if(!page.isPresent() && !size.isPresent()){
            for (User u: userRepository.findAll()) {
                users.add(u.getEmail());
            }
        }else{
            throw new BadRequestException();
        }

        return users;
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public void grantUser(String userID, AuthorizationDTO authorizationDTO, UserDetails loggedUser) throws BadRequestException, ForbiddenException {
        User currentUser=userRepository.findById(loggedUser.getUsername()).orElseThrow(() -> new BadRequestException());
        User changingUser=userRepository.findById(userID).orElseThrow(() -> new BadRequestException());


        AuthorizationManager.authorizeLineAccess(currentUser, lineRepository.getByName(authorizationDTO.getLineName()));

        //check if not already ADMIN of another Line
        if(!changingUser.getRoles().contains("ROLE_ADMIN")){
            changingUser.getRoles().add("ROLE_ADMIN");
        }
        changingUser.getLines().add(lineRepository.getByName(authorizationDTO.getLineName()));

        userRepository.save(changingUser);
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public void revokeUser(String userID, AuthorizationDTO authorizationDTO, UserDetails loggedUser)
            throws BadRequestException, ForbiddenException {
        User currentUser=userRepository.findById(loggedUser.getUsername()).orElseThrow(() -> new BadRequestException());
        User changingUser=userRepository.findById(userID).orElseThrow(() -> new BadRequestException());

        AuthorizationManager.authorizeLineAccess(currentUser, lineRepository.getByName(authorizationDTO.getLineName()));

        if(changingUser.getLines().size()==1){
            changingUser.getRoles().remove("ROLE_ADMIN");
        }
        changingUser.getLines().remove(lineRepository.getByName(authorizationDTO.getLineName()));

        userRepository.save(changingUser);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        User user;
        ArrayList<String> roles;
        ArrayList<Line> lines;

        //create SYSTEM_ADMIN
        roles=new ArrayList<>();
        roles.add("ROLE_SYSTEM-ADMIN");
        user = User.builder()
                .email("user0@email.it")
                .enabled(true)
                .roles(roles)
                .password(passwordEncoder.encode("qwerty"))
                .build();

        userRepository.save(user);

        //create ADMIN of Line1
        roles=new ArrayList<>();
        lines=new ArrayList<>();
        roles.add("ROLE_ADMIN");
        roles.add("ROLE_USER");
        lines.add(lineRepository.getByName("Line1"));
        user = User.builder()
                .email("user1@email.it")
                .enabled(true)
                .roles(roles)
                .lines(lines)
                .password(passwordEncoder.encode("qwerty"))
                .build();

        userRepository.save(user);

        //create 5 USERS
        for(int i=2; i<5;i++){
            roles= new ArrayList<>();
            roles.add("ROLE_USER");
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
        User user = userRepository.findById(s).orElseThrow(() -> new UsernameNotFoundException("Email " + s + " not found"));

        /* Retrieve list of authorities from roles */
        List<SimpleGrantedAuthority> authList = user.getRoles().stream()
                                                               .map((r) -> new SimpleGrantedAuthority(r))
                                                               .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.isEnabled(),
                true, true, true, authList);
    }

    public String getUser(String userID) throws NotFoundException {
        User u = userRepository.findById(userID).orElse(null);
        if(u != null)
            return u.getEmail();

        throw new NotFoundException();
    }
}
