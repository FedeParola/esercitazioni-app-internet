package it.polito.ai.esercitazione3.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends Exception{
    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException() {
    }
}
