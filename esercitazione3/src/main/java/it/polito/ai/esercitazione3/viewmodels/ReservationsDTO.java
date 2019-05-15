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
        private final List<UserStudentPair> students = new ArrayList<>();

        public void addStudent(String user, String student) {
            students.add(new UserStudentPair(user, student));
        }

        @Data
        public class UserStudentPair {
            private final String user;
            private final String student;
        }
    }
}
