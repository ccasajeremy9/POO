package pe.edu.upeu.celmax.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    private String idCliente;
    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos")
    private String dni;
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60, message = "El nombre admite máximo 60 caracteres")
    private String nombre;
    @NotBlank(message = "El celular es obligatorio")
    @Pattern(regexp = "9\\d{8}", message = "El celular debe tener 9 dígitos y empezar con 9")
    private String celular;
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no es válido")
    private String correo;
}
