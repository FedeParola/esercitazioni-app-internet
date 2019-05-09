package it.polito.ai.esercitazione3.viewmodels;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class LoginDTO {
    @Email
    @NotNull
    private String email;
    @NotNull
    private String pass;
}
