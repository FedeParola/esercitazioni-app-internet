package it.polito.ai.esercitazione3.entities;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
public class User {
    @Id
    @Column(name = "Email")
    private String email;

    @Column(name = "Name")
    private String name;

    @Column(name = "Surname")
    private String surname;

    @Column(name = "Psw")
    private String psw;

    @Column(name = "Confirmed")
    private boolean confirmed;

    @Column(name = "Enabled")
    private boolean enabled;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();
}
