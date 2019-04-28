package it.polito.ai.esercitazione2.controllers;

import it.polito.ai.esercitazione2.exceptions.BadRequestException;
import it.polito.ai.esercitazione2.exceptions.NotFoundException;
import it.polito.ai.esercitazione2.services.ReservationService;
import it.polito.ai.esercitazione2.viewmodels.ReservationDTO;
import it.polito.ai.esercitazione2.viewmodels.ReservationsDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
                                           @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) throws NotFoundException {
        return reservationService.getReservations(lineName, date);
    }

    @RequestMapping(value = "/reservations/{lineName}/{date}", method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Long createReservation(@PathVariable String lineName, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
                                  @RequestBody @Valid ReservationDTO reservation, BindingResult bindingResult,
                                  HttpServletResponse response) throws BadRequestException, NotFoundException {
        if(bindingResult.hasErrors()) {
            StringBuilder errMsg = new StringBuilder("Invalid format of the request body:");
            for (FieldError err : bindingResult.getFieldErrors()) {
                errMsg.append(" " + err.getField() + ": " + err.getDefaultMessage() + ";");
            }
            throw new BadRequestException(errMsg.toString());
        }

        Long reservationId = reservationService.addReservation(reservation, lineName, date);
        response.setStatus(HttpServletResponse.SC_CREATED);

        return reservationId;
    }

    @RequestMapping(value = "/reservations/{lineName}/{date}/{reservationId}", method = RequestMethod.PUT)
    public void updateReservation(@PathVariable String lineName, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date, @PathVariable Long reservationId,
                                  @RequestBody ReservationDTO reservation) throws NotFoundException, BadRequestException {
        reservationService.updateReservation(lineName, date, reservationId, reservation);

        return;
    }

    @RequestMapping(value = "/reservations/{lineName}/{date}/{reservationId}", method = RequestMethod.DELETE)
    public void deleteReservation(@PathVariable String lineName, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
                                  @PathVariable Long reservationId) throws NotFoundException {
        reservationService.deleteReservation(lineName, date, reservationId);

        return;
    }

    @RequestMapping(value = "/reservations/{lineName}/{date}/{reservationId}", method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ReservationDTO getReservation(@PathVariable String lineName,
                                         @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
                                         @PathVariable Long reservationId) throws NotFoundException {
        return reservationService.getReservation(lineName, date, reservationId);
    }
}
