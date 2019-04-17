package it.polito.ai.esercitazione2.controllers;

import it.polito.ai.esercitazione2.services.ReservationService;
import it.polito.ai.esercitazione2.viewmodels.ReservationDTO;
import it.polito.ai.esercitazione2.viewmodels.ReservationsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;

@RestController
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @RequestMapping(value = "/reservations/{lineName}/{date}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ReservationsDTO getReservations(@PathVariable String lineName,
                                           @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
                                           HttpServletResponse response) {
        ReservationsDTO reservations = reservationService.getReservations(lineName, date);
        if(reservations == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

        return reservations;
    }

    @RequestMapping(value = "/reservations/{lineName}/{date}", method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Long createReservation(@PathVariable String lineName, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
                                  @RequestBody @Valid ReservationDTO reservation, BindingResult bindingResult,
                                  HttpServletResponse response) {
        if(bindingResult.hasErrors()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        Long reservationId = reservationService.addReservation(reservation, lineName, date);
        if(reservationId == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            response.setStatus(HttpServletResponse.SC_CREATED);
        }

        return reservationId;
    }

    @RequestMapping(value = "/reservations/{lineName}/{date}/{reservationId}", method = RequestMethod.PUT)
    public void updateReservation(@PathVariable String lineName, @PathVariable Date date, @PathVariable Long reservationId,
                                  @RequestBody ReservationDTO reservation, HttpServletResponse response) {
        if(!reservationService.updateReservation(lineName, date, reservationId, reservation)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

        return;
    }

    @RequestMapping(value = "/reservations/{lineName}/{date}/{reservationId}", method = RequestMethod.DELETE)
    public void deleteReservation(@PathVariable String lineName, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
                                  @PathVariable Long reservationId, HttpServletResponse response) {
        if(!reservationService.deleteReservation(lineName, date, reservationId)){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

        return;
    }

    @RequestMapping(value = "/reservations/{lineName}/{date}/{reservationId}", method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ReservationDTO getReservation(@PathVariable String lineName,
                                         @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
                                         @PathVariable Long reservationId,
                                         HttpServletResponse response) {
        ReservationDTO reservation = reservationService.getReservation(lineName, date, reservationId);
        if(reservation == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

        return reservation;
    }
}
