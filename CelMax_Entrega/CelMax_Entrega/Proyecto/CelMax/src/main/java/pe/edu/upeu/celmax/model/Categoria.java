package pe.edu.upeu.celmax.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {
    /** Códigos fijos cargados en data_celmax.sql */
    public static final String CELULARES = "CAT-01";
    public static final String ACCESORIOS = "CAT-02";

    private String idCategoria;
    private String nombre;
}
