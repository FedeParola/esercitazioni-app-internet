package it.polito.ai.esercitazione3.entities;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Data
public class ConfirmationToken {
    @Id
    @GeneratedValue
    @Column(name = "Id")
    private Long id;

    @OneToOne
    private User user;

    @Column(name = "UUID", unique = true, nullable = false)
    private String uuid;

    @Column(name = "ExpiryDate", nullable = false)
    private Timestamp expiryDate;

    public boolean isExpired() {
        return (new Date()).after(expiryDate);
    }
}
