package it.polito.ai.esercitazione2.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;

@Data
@Entity
public class Student {
    @Id
    @Column(name = "FiscalCode")
    private String fiscalCode;

    @Column(name = "FirstName", nullable = false)
    private String firstName;

    @Column(name = "LastName", nullable = false)
    private String lastName;

    @OneToMany(mappedBy = "key.student")
    private Set<Reservation> reservations;
}
