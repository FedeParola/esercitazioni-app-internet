package it.polito.ai.esercitazione2.services;

import it.polito.ai.esercitazione2.entities.Line;
import it.polito.ai.esercitazione2.entities.Reservation;
import it.polito.ai.esercitazione2.entities.Stop;
import it.polito.ai.esercitazione2.repositories.LineRepository;
import it.polito.ai.esercitazione2.repositories.ReservationRepository;
import it.polito.ai.esercitazione2.repositories.StopRepository;
import it.polito.ai.esercitazione2.viewmodels.ReservationDTO;
import it.polito.ai.esercitazione2.viewmodels.ReservationsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ReservationService {
    private static final Logger log = LoggerFactory.getLogger(LineService.class);
    @Autowired
    private StopRepository stopRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private LineRepository lineRepository;

    public ReservationsDTO getReservations(String lineName, Date date) {
        /* Get the requested line */
        Line line = lineRepository.getByName(lineName);
        if(line == null) {
            return null;
        }

        ReservationsDTO reservationsDTO = new ReservationsDTO();
        for(Stop stop: line.getStops()) {
            /* Prepare a list of reservations for the current stop */
            ReservationsDTO.StopReservations stopReservations = new ReservationsDTO.StopReservations();
            stopReservations.setStopName(stop.getName());

            /* Add reservations for the requested date to the list */
            for(Reservation reservation: stop.getReservations()) {
                /* WARNING: not efficient, maybe rewrite the whole method */
                if(date.equals(reservation.getDate())) {
                    stopReservations.getStudents().add(reservation.getStudent());
                }
            }

            /* Add the reservations to the correct direction */
            if(stop.getDirection() == 'O') {
                reservationsDTO.getOutwardReservations().add(stopReservations);
            } else {
                reservationsDTO.getReturnReservations().add(stopReservations);
            }
        }

        return reservationsDTO;
    }

    public Long addReservation(ReservationDTO reservationDTO, String lineName, Date date) {
        Stop stop = stopRepository.findById(reservationDTO.getStopId()).orElse(null);
        if(stop == null) {
            return null;
        }

        Line line = lineRepository.getByName(lineName);
        if(line == null) {
            return null;
        }

        if(!line.equals(stop.getLine())) {
            return null;
        }

        if(reservationDTO.getDirection().charAt(0) != stop.getDirection().charValue()) {
            return null;
        }

        Reservation reservation = new Reservation();
        reservation.setStop(stop);
        reservation.setDate(new java.sql.Date(date.getTime()));
        reservation.setStudent(reservationDTO.getStudent());

        reservation = reservationRepository.save(reservation);

        return reservation.getId();
    }

    public boolean updateReservation(String lineName, Date date, Long reservationId, ReservationDTO reservationDTO) {
        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
        if(reservation == null) {
            return false;
        }

        if(!date.equals(reservation.getDate()) || !reservation.getStop().getLine().getName().equals(lineName)) {
            return false;
        }

        /* Update the stop */
        if(!reservationDTO.getStopId().equals(reservation.getStop().getId())) {
            Stop stop = stopRepository.findById(reservationDTO.getStopId()).orElse(null);
            if(stop == null) {
                return false;
            }

            if(reservationDTO.getDirection().charAt(0) != stop.getDirection().charValue()) {
                return false;
            }

            reservation.setStop(stop);
        }

        /* Update other fields */
        reservation.setStudent(reservationDTO.getStudent());

        reservationRepository.save(reservation);

        return true;
    }

    public boolean deleteReservation(String lineName, Date date, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
        if(reservation == null) {
            return false;
        }

        if(!date.equals(reservation.getDate()) || !reservation.getStop().getLine().getName().equals(lineName)) {
            return false;
        }

        reservationRepository.delete(reservation);

        return true;
    }

    public ReservationDTO getReservation(String lineName, Date date, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
        if(reservation == null) {
            return null;
        }

        if(!date.equals(reservation.getDate()) || !reservation.getStop().getLine().getName().equals(lineName)) {
            return null;
        }

        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setId(reservation.getId());
        reservationDTO.setDirection(reservation.getStop().getDirection().toString());
        reservationDTO.setStopId(reservation.getStop().getId());
        reservationDTO.setStudent(reservation.getStudent());

        return reservationDTO;
    }
}
