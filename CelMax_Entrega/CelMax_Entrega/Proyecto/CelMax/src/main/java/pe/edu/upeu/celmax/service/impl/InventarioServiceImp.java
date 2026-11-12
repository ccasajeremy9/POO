package pe.edu.upeu.celmax.service.impl;

import pe.edu.upeu.celmax.model.Inventario;
import pe.edu.upeu.celmax.repository.InventarioRepository;
import pe.edu.upeu.celmax.repository.ICrudGenericoRepository;
import pe.edu.upeu.celmax.service.IInventarioService;


public class InventarioServiceImp extends CrudGenericoServiceImp<Inventario, String> implements IInventarioService {

    private final InventarioRepository repo;

    public InventarioServiceImp(InventarioRepository repo) {
        this.repo = repo;
    }

    @Override
    protected ICrudGenericoRepository<Inventario, String> getRepo() {
        return repo;
    }
}
