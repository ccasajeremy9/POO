package pe.edu.upeu.celmax.service;

import pe.edu.upeu.celmax.dto.ComboBoxOption;
import pe.edu.upeu.celmax.model.Accesorio;

import java.util.List;

public interface IAccesorioService extends ICrudGenericoService<Accesorio, String> {
    List<ComboBoxOption> listarCombobox();
}
