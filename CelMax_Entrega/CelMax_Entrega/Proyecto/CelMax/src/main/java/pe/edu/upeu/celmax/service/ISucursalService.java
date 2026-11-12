package pe.edu.upeu.celmax.service;

import pe.edu.upeu.celmax.dto.ComboBoxOption;
import pe.edu.upeu.celmax.model.Sucursal;

import java.util.List;

public interface ISucursalService extends ICrudGenericoService<Sucursal, String> {
    List<ComboBoxOption> listarCombobox();
}
