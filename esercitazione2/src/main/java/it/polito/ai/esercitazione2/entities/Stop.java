package it.polito.ai.esercitazione2.entities;

import lombok.Data;

import javax.persistence.*;
import java.sql.Time;
import java.util.Set;

@Entity
@Data
public class Stop {
    @Id
    @GeneratedValue
    @Column(name = "Id")
    private Long id;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "Order", nullable = false)
    private Integer order;

    @Column(name = "Direction", nullable = false)
    private Character direction;

    @Column(name = "Time", nullable = false)
    private Time time;

    @ManyToOne
    @JoinColumn(name = "LineId", nullable = false)
    private Line line;

    @OneToMany(mappedBy = "key.stop")
    private Set<Reservation> reservations;
}
