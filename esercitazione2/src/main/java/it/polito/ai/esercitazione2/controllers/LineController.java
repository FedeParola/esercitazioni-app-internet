package it.polito.ai.esercitazione2.controllers;

import it.polito.ai.esercitazione2.exceptions.NotFoundException;
import it.polito.ai.esercitazione2.services.LineService;
import it.polito.ai.esercitazione2.viewmodels.LineDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class LineController {
    @Autowired
    private LineService lineService;

    @RequestMapping(value = "/lines", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<String> getLines() {
        return lineService.getLineNames();
    }

    @RequestMapping(value = "/lines/{lineName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public LineDTO getLine(@PathVariable String lineName) throws NotFoundException {
        LineDTO line = lineService.getLine(lineName);

        return line;
    }
}
