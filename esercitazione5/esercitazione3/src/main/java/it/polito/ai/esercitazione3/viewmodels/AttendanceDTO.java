package it.polito.ai.esercitazione3.viewmodels;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class AttendanceDTO {
    @NotNull
    @Min(0)
    private Long pupilId;

    @NotNull
    @Pattern(regexp = "O|R")
    private Character direction;
}
