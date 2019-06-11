package it.polito.ai.esercitazione3.viewmodels;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AuthorizationDTO {

    @NotNull
    private String action;
    @NotNull
    private String lineName;
}
