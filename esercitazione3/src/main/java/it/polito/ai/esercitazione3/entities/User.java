package it.polito.ai.esercitazione3.entities;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
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

    @ManyToMany
    @JoinTable(name="Role",
            joinColumns = {@JoinColumn(name="Name")},
            inverseJoinColumns ={@JoinColumn(name="Email")})
    private List<Role> roles;
}
