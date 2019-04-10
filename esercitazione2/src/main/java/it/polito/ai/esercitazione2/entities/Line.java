package it.polito.ai.esercitazione2.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
public class Line {
    @Id
    @GeneratedValue
    @Column(name = "Id")
    private Long id;

    @Column(name = "Name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "line")
    private Set<Stop> stops;
}
