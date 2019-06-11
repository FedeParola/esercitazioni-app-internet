package it.polito.ai.esercitazione3.viewmodels;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RecoverDTO {
    @NotNull
    private String pass;
    @NotNull
    private String confPass;
}

