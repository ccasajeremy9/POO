package pe.edu.upeu.celmax.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Celular {
    private String idCelular;
    @NotBlank(message = "El modelo es obligatorio")
    private String modelo;
    @NotBlank(message = "La marca es obligatoria")
    private String marca;
    @NotNull(message = "El almacenamiento es obligatorio")
    @Positive(message = "El almacenamiento debe ser mayor a 0")
    private Integer almacenamiento;
    @NotNull(message = "La RAM es obligatoria")
    @Positive(message = "La RAM debe ser mayor a 0")
    private Integer ram;
    @NotNull(message = "La categoría es obligatoria")
    private Categoria idCategoria;
}
