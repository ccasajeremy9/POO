package pe.edu.upeu.celmax.service;

import pe.edu.upeu.celmax.dto.ComboBoxOption;
import pe.edu.upeu.celmax.model.Categoria;

import java.util.List;

public interface ICategoriaService extends ICrudGenericoService<Categoria, String> {
    List<ComboBoxOption> listarCombobox();
}
