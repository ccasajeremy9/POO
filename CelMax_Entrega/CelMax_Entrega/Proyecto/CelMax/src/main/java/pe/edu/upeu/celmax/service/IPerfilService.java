package pe.edu.upeu.celmax.service;

import pe.edu.upeu.celmax.dto.ComboBoxOption;
import pe.edu.upeu.celmax.model.Perfil;

import java.util.List;

public interface IPerfilService extends ICrudGenericoService<Perfil, String> {
    List<ComboBoxOption> listarCombobox();
}
