package it.polito.ai.esercitazione1;

import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

public class ErrorMessageBuilder {
    public static String build(List<FieldError> errorList){
        return errorList.stream().map((e) -> e.getDefaultMessage())
                                .collect(Collectors.joining("; "));
    }
}
