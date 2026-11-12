package pe.edu.upeu.celmax.service.impl;

import pe.edu.upeu.celmax.dto.ComboBoxOption;
import pe.edu.upeu.celmax.model.Sucursal;
import pe.edu.upeu.celmax.repository.SucursalRepository;
import pe.edu.upeu.celmax.repository.ICrudGenericoRepository;
import pe.edu.upeu.celmax.service.ISucursalService;

import java.util.ArrayList;
import java.util.List;

public class SucursalServiceImp extends CrudGenericoServiceImp<Sucursal, String> implements ISucursalService {

    private final SucursalRepository repo;

    public SucursalServiceImp(SucursalRepository repo) {
        this.repo = repo;
    }

    @Override
    protected ICrudGenericoRepository<Sucursal, String> getRepo() {
        return repo;
    }

    @Override
    public List<ComboBoxOption> listarCombobox() {
        List<ComboBoxOption> listar = new ArrayList<>();
        for (Sucursal m : repo.findAll()) {
            listar.add(new ComboBoxOption(m.getIdSucursal(), "Tienda " + m.getNumeroTienda() + " - " + m.getCiudad()));
        }
        return listar;
    }
}
