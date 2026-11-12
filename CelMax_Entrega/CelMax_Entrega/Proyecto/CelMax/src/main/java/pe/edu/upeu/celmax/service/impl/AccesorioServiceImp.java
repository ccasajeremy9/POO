package pe.edu.upeu.celmax.service.impl;

import pe.edu.upeu.celmax.dto.ComboBoxOption;
import pe.edu.upeu.celmax.model.Accesorio;
import pe.edu.upeu.celmax.repository.AccesorioRepository;
import pe.edu.upeu.celmax.repository.ICrudGenericoRepository;
import pe.edu.upeu.celmax.service.IAccesorioService;

import java.util.ArrayList;
import java.util.List;

public class AccesorioServiceImp extends CrudGenericoServiceImp<Accesorio, String> implements IAccesorioService {

    private final AccesorioRepository repo;

    public AccesorioServiceImp(AccesorioRepository repo) {
        this.repo = repo;
    }

    @Override
    protected ICrudGenericoRepository<Accesorio, String> getRepo() {
        return repo;
    }

    @Override
    public List<ComboBoxOption> listarCombobox() {
        List<ComboBoxOption> listar = new ArrayList<>();
        for (Accesorio m : repo.findAll()) {
            listar.add(new ComboBoxOption(m.getIdAccesorio(), m.getNombre()));
        }
        return listar;
    }
}
