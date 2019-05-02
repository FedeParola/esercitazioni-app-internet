package it.polito.ai.esercitazione3.viewmodels;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReservationsDTO {
    private final List<StopReservations> outwardReservations = new ArrayList<>();
    private final List<StopReservations> returnReservations = new ArrayList<>();

    @Data
    public static class StopReservations {
        private String stopName;
        private final List<String> students = new ArrayList<>();
    }
}
