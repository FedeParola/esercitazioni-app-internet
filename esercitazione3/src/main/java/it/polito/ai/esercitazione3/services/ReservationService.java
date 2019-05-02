package it.polito.ai.esercitazione3.services;

import it.polito.ai.esercitazione3.entities.Line;
import it.polito.ai.esercitazione3.entities.Reservation;
import it.polito.ai.esercitazione3.entities.Stop;
import it.polito.ai.esercitazione3.exceptions.BadRequestException;
import it.polito.ai.esercitazione3.exceptions.NotFoundException;
import it.polito.ai.esercitazione3.repositories.LineRepository;
import it.polito.ai.esercitazione3.repositories.ReservationRepository;
import it.polito.ai.esercitazione3.repositories.StopRepository;
import it.polito.ai.esercitazione3.viewmodels.ReservationDTO;
import it.polito.ai.esercitazione3.viewmodels.ReservationsDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class ReservationService {
    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);
    @Autowired
    private StopRepository stopRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private LineRepository lineRepository;

    public ReservationsDTO getReservations(String lineName, Date date) throws NotFoundException {
        /* Get the requested line */
        Line line = lineRepository.getByName(lineName);
        if(line == null) {
            throw new NotFoundException("Line " + lineName + " not found");
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

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    public Long addReservation(ReservationDTO reservationDTO, String lineName, Date date) throws BadRequestException, NotFoundException {
        Line line = lineRepository.getByName(lineName);
        if(line == null) {
            throw new NotFoundException("Line " + lineName + " not found");
        }

        Stop stop = stopRepository.findById(reservationDTO.getStopId()).orElse(null);
        if(stop == null) {
            throw new BadRequestException("Unknown stop with id " + reservationDTO.getStopId());
        }

        if(!line.equals(stop.getLine())) {
            throw new BadRequestException("Stop with id " + reservationDTO.getStopId() + "doesn't belong to line " + lineName);
        }

        if(reservationDTO.getDirection().charAt(0) != stop.getDirection().charValue()) {
            throw new BadRequestException("The requested stop isn't available for the requested direction");
        }

        Reservation reservation = new Reservation();
        reservation.setStop(stop);
        reservation.setDate(new java.sql.Date(date.getTime()));
        reservation.setStudent(reservationDTO.getStudent());

        reservation = reservationRepository.save(reservation);

        return reservation.getId();
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    public void updateReservation(String lineName, Date date, Long reservationId, ReservationDTO reservationDTO) throws NotFoundException, BadRequestException {
        Reservation reservation = getReservationFromUri(lineName, date, reservationId);

        /* Update the stop */
        if(!reservationDTO.getStopId().equals(reservation.getStop().getId())) {
            Stop stop = stopRepository.findById(reservationDTO.getStopId()).orElse(null);

            if(stop == null) {
                throw new BadRequestException("Unknown stop with id " + reservationDTO.getStopId());
            }

            if(!stop.getLine().getName().equals(lineName)) {
                throw new BadRequestException("Stop with id " + reservationDTO.getStopId() + "doesn't belong to line " + lineName);
            }

            if(reservationDTO.getDirection().charAt(0) != stop.getDirection().charValue()) {
                throw new BadRequestException("The requested stop isn't available for the requested direction");
            }

            reservation.setStop(stop);
        }

        /* Update other fields */
        reservation.setStudent(reservationDTO.getStudent());

        reservationRepository.save(reservation);

        return;
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    public void deleteReservation(String lineName, Date date, Long reservationId) throws NotFoundException {
        Reservation reservation = getReservationFromUri(lineName, date, reservationId);

        reservationRepository.delete(reservation);

        return;
    }

    public ReservationDTO getReservation(String lineName, Date date, Long reservationId) throws NotFoundException {
        Reservation reservation = getReservationFromUri(lineName, date, reservationId);

        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setId(reservation.getId());
        reservationDTO.setDirection(reservation.getStop().getDirection().toString());
        reservationDTO.setStopId(reservation.getStop().getId());
        reservationDTO.setStudent(reservation.getStudent());

        return reservationDTO;
    }

    private Reservation getReservationFromUri(String lineName, Date date, Long reservationId) throws NotFoundException {
        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);

        if(reservation == null) {
            throw new NotFoundException("Reservation with id " + reservationId + " not found");
        }

        if(!date.equals(reservation.getDate()) || !reservation.getStop().getLine().getName().equals(lineName)) {
            throw new NotFoundException("Reservation with id " + reservationId + " not found");
        }

        return reservation;
    }
}
