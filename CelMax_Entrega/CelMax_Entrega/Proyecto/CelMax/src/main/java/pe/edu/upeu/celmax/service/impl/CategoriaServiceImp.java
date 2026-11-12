package pe.edu.upeu.celmax.service.impl;

import pe.edu.upeu.celmax.dto.ComboBoxOption;
import pe.edu.upeu.celmax.model.Categoria;
import pe.edu.upeu.celmax.repository.CategoriaRepository;
import pe.edu.upeu.celmax.repository.ICrudGenericoRepository;
import pe.edu.upeu.celmax.service.ICategoriaService;

import java.util.ArrayList;
import java.util.List;

public class CategoriaServiceImp extends CrudGenericoServiceImp<Categoria, String> implements ICategoriaService {

    private final CategoriaRepository repo;

    public CategoriaServiceImp(CategoriaRepository repo) {
        this.repo = repo;
    }

    @Override
    protected ICrudGenericoRepository<Categoria, String> getRepo() {
        return repo;
    }

    @Override
    public List<ComboBoxOption> listarCombobox() {
        List<ComboBoxOption> listar = new ArrayList<>();
        for (Categoria m : repo.findAll()) {
            listar.add(new ComboBoxOption(m.getIdCategoria(), m.getNombre()));
        }
        return listar;
    }
}
