package pe.edu.upeu.celmax.service.impl;

import pe.edu.upeu.celmax.dto.ComboBoxOption;
import pe.edu.upeu.celmax.model.Cliente;
import pe.edu.upeu.celmax.repository.ClienteRepository;
import pe.edu.upeu.celmax.repository.ICrudGenericoRepository;
import pe.edu.upeu.celmax.service.IClienteService;

import java.util.ArrayList;
import java.util.List;

public class ClienteServiceImp extends CrudGenericoServiceImp<Cliente, String> implements IClienteService {

    private final ClienteRepository repo;

    public ClienteServiceImp(ClienteRepository repo) {
        this.repo = repo;
    }

    @Override
    protected ICrudGenericoRepository<Cliente, String> getRepo() {
        return repo;
    }

    @Override
    public List<ComboBoxOption> listarCombobox() {
        List<ComboBoxOption> listar = new ArrayList<>();
        for (Cliente m : repo.findAll()) {
            listar.add(new ComboBoxOption(m.getIdCliente(), m.getDni() + " - " + m.getNombre()));
        }
        return listar;
    }
}
