package pe.edu.upeu.celmax.service;

import pe.edu.upeu.celmax.dto.ComboBoxOption;
import pe.edu.upeu.celmax.model.Cliente;

import java.util.List;

public interface IClienteService extends ICrudGenericoService<Cliente, String> {
    List<ComboBoxOption> listarCombobox();
}
