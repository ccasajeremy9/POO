package pe.edu.upeu.sysnacimientos.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActaNacimiento {

    private Long idActa;
    @NotBlank(message = "El nombre del recién nacido es obligatorio")
    private String nombreRecienNacido;
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
    private LocalDate fechaNacimiento;
    @NotBlank(message = "El hospital o lugar de nacimiento es obligatorio")
    private String lugarNacimiento;
    @NotBlank(message = "El nombre del padre es obligatorio")
    private String nombrePadre;
    @NotBlank(message = "El nombre de la madre es obligatorio")
    private String nombreMadre;
}
