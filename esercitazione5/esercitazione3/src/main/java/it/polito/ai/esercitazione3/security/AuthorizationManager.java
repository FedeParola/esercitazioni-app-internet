package it.polito.ai.esercitazione3.security;

import it.polito.ai.esercitazione3.entities.Line;
import it.polito.ai.esercitazione3.entities.Reservation;
import it.polito.ai.esercitazione3.entities.User;
import it.polito.ai.esercitazione3.exceptions.ForbiddenException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AuthorizationManager {
    public static void authorizeReservationAccess(User user, Reservation reservation) throws ForbiddenException {
        if(!user.getEmail().equals(reservation.getPupil().getUser().getEmail())){
            authorizeLineAccess(user, reservation.getStop().getLine());
        }
    }

    public static void authorizeLineAccess(User user, Line line) throws ForbiddenException {
        List<String> userLinesNames = new ArrayList<>();

        if(!user.getRoles().contains("ROLE_SYSTEM-ADMIN")) {
            if(!(user.getLines().stream()
                                .map((s)-> s.getName())
                                .collect(Collectors.toList())
                                .contains(line.getName()))){
                throw new ForbiddenException();
            }
        }
    }
}
