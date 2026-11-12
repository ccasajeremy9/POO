package pe.edu.upeu.celmax.service.impl;

import pe.edu.upeu.celmax.dto.ComboBoxOption;
import pe.edu.upeu.celmax.model.Perfil;
import pe.edu.upeu.celmax.repository.PerfilRepository;
import pe.edu.upeu.celmax.repository.ICrudGenericoRepository;
import pe.edu.upeu.celmax.service.IPerfilService;

import java.util.ArrayList;
import java.util.List;

public class PerfilServiceImp extends CrudGenericoServiceImp<Perfil, String> implements IPerfilService {

    private final PerfilRepository repo;

    public PerfilServiceImp(PerfilRepository repo) {
        this.repo = repo;
    }

    @Override
    protected ICrudGenericoRepository<Perfil, String> getRepo() {
        return repo;
    }

    @Override
    public List<ComboBoxOption> listarCombobox() {
        List<ComboBoxOption> listar = new ArrayList<>();
        for (Perfil m : repo.findAll()) {
            listar.add(new ComboBoxOption(m.getIdPerfil(), m.getNombrePerfil()));
        }
        return listar;
    }
}
