package it.polito.ai.esercitazione2.controllers;

import it.polito.ai.esercitazione2.entities.Line;
import it.polito.ai.esercitazione2.entities.Stop;
import it.polito.ai.esercitazione2.repositories.LineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Time;

@RestController
public class LinesController {
    @Autowired
    private LineRepository repo;

    @RequestMapping(value = "/lines", method = RequestMethod.GET)
    public void test() {
        Line l = new Line();
        l.setName("Line1");

        Stop s = new Stop();
        s.setName("Stop1");
        s.setDirection('R');
        s.setPosition(2);
        s.setTime(new Time(10, 0, 0));
        s.setLine(l);

        repo.save(l);

        System.out.println(l.getId());

        return;
    }

}
