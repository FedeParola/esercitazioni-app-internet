package it.polito.ai.esercitazione3.viewmodels;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RegistrationDTO {
    @Email
    @Size(min=1,max=255)
    private String email;
    @Size(min=6,max=32)
    private String pass;
    @Size(min=6,max=32)
    private String confPass;
}
