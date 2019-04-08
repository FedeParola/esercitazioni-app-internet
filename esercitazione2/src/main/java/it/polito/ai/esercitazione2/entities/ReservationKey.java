package it.polito.ai.esercitazione2.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ReservationKey implements Serializable{
    @Column(name = "StudentId")
    private Student student;

    @Column(name = "StopId")
    private Stop stop;


}
