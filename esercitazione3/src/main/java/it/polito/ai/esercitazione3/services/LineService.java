package it.polito.ai.esercitazione3.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.ai.esercitazione3.entities.Line;
import it.polito.ai.esercitazione3.entities.Stop;
import it.polito.ai.esercitazione3.exceptions.NotFoundException;
import it.polito.ai.esercitazione3.repositories.LineRepository;
import it.polito.ai.esercitazione3.repositories.StopRepository;
import it.polito.ai.esercitazione3.viewmodels.LineDTO;
import it.polito.ai.esercitazione3.viewmodels.StopDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.net.URL;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class LineService implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(LineService.class);
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StopRepository stopRepository;

    public Set<String> getLineNames() {
        Set<String> names = new HashSet<>();

        for (Line l: lineRepository.findAll()) {
            names.add(l.getName());
        }

        return names;
    }

    public LineDTO getLine(String lineName) throws NotFoundException {
        /* Get requested line */
        Line line = lineRepository.getByName(lineName);
        if(line == null) {
            throw new NotFoundException("Line " + lineName + " not found");
        }

        LineDTO lineDTO = new LineDTO();
        List<StopDTO> outwardStops = new ArrayList<>();
        List<StopDTO> returnStops = new ArrayList<>();

        /* Map every stop entity into a DTO and add it to the proper list */
        for (Stop stop: line.getStops()) {
            StopDTO stopDTO = new StopDTO();

            stopDTO.setId(stop.getId());
            stopDTO.setName(stop.getName());
            stopDTO.setPosition(stop.getPosition());
            stopDTO.setTime(new SimpleDateFormat("HH:mm").format(stop.getTime()));

            if(stop.getDirection() == 'O') {
                outwardStops.add(stopDTO);
            } else {
                returnStops.add(stopDTO);
            }
        }

        /* Add stop lists to the line DTO */
        lineDTO.setOutwardStops(outwardStops);
        lineDTO.setReturnStops(returnStops);

        return lineDTO;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        /* Find lines dir resource */
        URL linesDirURL = this.getClass().getResource("/lines");

        /* Check dir existence */
        if(linesDirURL == null) {
            log.error("Cannot access lines directory");

        } else {
            File linesDir = new File(linesDirURL.getFile());

            /* Check dir existence */
            if (!linesDir.isDirectory()) {
                log.error("Cannot access lines directory");

            } else {
                ObjectMapper objectMapper = new ObjectMapper();
                Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


                /* Retrieve all json files in the dir */
                Pattern p = Pattern.compile(".*\\.json");
                File[] linesFiles = linesDir.listFiles((dir, name) -> p.matcher(name).matches());

                /* Parse every file */
                for (File f: linesFiles) {
                    try {
                        /* Read the json into a DTO */
                        LineDTO lineDTO = objectMapper.readValue(f, LineDTO.class);

                        /* Check DTO validity */
                        Set<ConstraintViolation<LineDTO>> violations = validator.validate(lineDTO);
                        if (violations.size() > 0) {
                            StringBuilder err = new StringBuilder("Errors validating file " + f.getName() + ":");
                            for (ConstraintViolation<LineDTO> violation : violations) {
                                err.append("\n" + violation.getPropertyPath() + ": " + violation.getMessage());
                            }
                            log.error(err.toString());

                        } else {
                            /* Try to add line and stops to the DB */
                            try {
                                addLine(lineDTO);
                                log.info("Line " + lineDTO.getName() + " added to the DB");
                            } catch(Exception e) {
                                log.error("Error adding line " + lineDTO.getName() + " to the DB: " + e.getMessage());
                            }
                        }

                    } catch (JsonProcessingException e) {
                        log.error("Errors parsing file " + f.getName() + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    protected void addLine(LineDTO lineDTO) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("H:m");

        /* Map line DTO to entity and persist it */
        Line line = new Line();
        line.setName(lineDTO.getName());
        line = lineRepository.save(line);

        /* Map outward stops DTOs to entities and persist them */
        if (lineDTO.getOutwardStops() != null) {
            for (StopDTO stopDTO: lineDTO.getOutwardStops()) {
                Stop stop = new Stop();
                stop.setName(stopDTO.getName());
                stop.setPosition(stopDTO.getPosition());
                stop.setDirection('O');
                stop.setTime(new Time(sdf.parse(stopDTO.getTime()).getTime()));
                stop.setLine(line);

                stopRepository.save(stop);
            }
        }

        /* Map return stops DTOs to entities and persist them */
        if (lineDTO.getReturnStops() != null) {
            for (StopDTO stopDTO: lineDTO.getReturnStops()) {
                Stop stop = new Stop();
                stop.setName(stopDTO.getName());
                stop.setPosition(stopDTO.getPosition());
                stop.setDirection('R');
                stop.setTime(new Time(sdf.parse(stopDTO.getTime()).getTime()));
                stop.setLine(line);

                stopRepository.save(stop);
            }
        }

        return;
    }
}
