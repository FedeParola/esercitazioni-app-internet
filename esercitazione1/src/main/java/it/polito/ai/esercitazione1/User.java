package it.polito.ai.esercitazione1;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private String name;
    private String surname;
    private final String email;
    private String pass;
}
