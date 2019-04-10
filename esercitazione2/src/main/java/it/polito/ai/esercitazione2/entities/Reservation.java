package it.polito.ai.esercitazione2.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Data
public class Reservation {
    @EmbeddedId
    private ReservationKey key;

    @Embeddable
    public class ReservationKey implements Serializable {
        @ManyToOne
        @JoinColumn(name = "StudentId", nullable = false)
        private Student student;

        @ManyToOne
        @JoinColumn(name = "StopId", nullable = false)
        private Stop stop;

        @Column(name = "DateTime", nullable = false)
        private Timestamp dateTime;
    }
}
