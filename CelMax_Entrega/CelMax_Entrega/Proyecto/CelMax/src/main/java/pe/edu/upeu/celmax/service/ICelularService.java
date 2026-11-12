package pe.edu.upeu.celmax.service;

import pe.edu.upeu.celmax.dto.ComboBoxOption;
import pe.edu.upeu.celmax.model.Celular;

import java.util.List;

public interface ICelularService extends ICrudGenericoService<Celular, String> {
    List<ComboBoxOption> listarCombobox();
}
