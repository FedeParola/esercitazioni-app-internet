package it.polito.ai.esercitazione3.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Pupil {
    @Id
    @GeneratedValue
    @Column(name = "Id")
    private Long id;

    @Column(name = "Name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "UserId", nullable = false)
    private User user;
}
