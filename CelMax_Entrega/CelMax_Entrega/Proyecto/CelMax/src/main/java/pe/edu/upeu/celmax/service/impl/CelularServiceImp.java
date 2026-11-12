package pe.edu.upeu.celmax.service.impl;

import pe.edu.upeu.celmax.dto.ComboBoxOption;
import pe.edu.upeu.celmax.model.Celular;
import pe.edu.upeu.celmax.repository.CelularRepository;
import pe.edu.upeu.celmax.repository.ICrudGenericoRepository;
import pe.edu.upeu.celmax.service.ICelularService;

import java.util.ArrayList;
import java.util.List;

public class CelularServiceImp extends CrudGenericoServiceImp<Celular, String> implements ICelularService {

    private final CelularRepository repo;

    public CelularServiceImp(CelularRepository repo) {
        this.repo = repo;
    }

    @Override
    protected ICrudGenericoRepository<Celular, String> getRepo() {
        return repo;
    }

    @Override
    public List<ComboBoxOption> listarCombobox() {
        List<ComboBoxOption> listar = new ArrayList<>();
        for (Celular m : repo.findAll()) {
            listar.add(new ComboBoxOption(m.getIdCelular(), m.getMarca() + " " + m.getModelo()));
        }
        return listar;
    }
}
