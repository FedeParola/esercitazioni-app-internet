package it.polito.ai.esercitazione3.services;

import it.polito.ai.esercitazione3.entities.User;
import it.polito.ai.esercitazione3.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;


@Service
public class UserService implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(LineService.class);
    @Autowired
    private UserRepository userRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        User user;
        ArrayList<String> rules;

        rules=new ArrayList<>();
        rules.add("system-admin");
        user = User.builder()
                .email("user0@email.it")
                .name("user1")
                .surname("surname1")
                .confirmed(true)
                .enabled(true)
                .roles(rules)
                .psw("qwerty")
                .build();

        for(int i=1; i<5;i++){
            rules= new ArrayList<>();
            rules.add("user");
            user = User.builder()
                    .email("user"+i+"@email.it")
                    .name("user1")
                    .surname("surname1")
                    .confirmed(true)
                    .enabled(true)
                    .roles(rules)
                    .psw("qwerty")
                    .build();
        }
    }
}
