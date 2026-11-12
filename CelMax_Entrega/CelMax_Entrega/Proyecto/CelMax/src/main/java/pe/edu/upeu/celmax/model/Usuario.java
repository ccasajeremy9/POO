package pe.edu.upeu.celmax.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private String idUsuario;
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 30, message = "El usuario admite máximo 30 caracteres")
    private String nombreUsuario;
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 30, message = "La contraseña debe tener entre 6 y 30 caracteres")
    private String contrasena;
    @NotNull(message = "El perfil es obligatorio")
    private Perfil idPerfil;
}
