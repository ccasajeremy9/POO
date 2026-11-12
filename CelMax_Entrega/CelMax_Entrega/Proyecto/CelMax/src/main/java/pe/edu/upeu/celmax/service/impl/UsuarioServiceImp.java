package pe.edu.upeu.celmax.service.impl;

import pe.edu.upeu.celmax.model.Usuario;
import pe.edu.upeu.celmax.repository.UsuarioRepository;
import pe.edu.upeu.celmax.repository.ICrudGenericoRepository;
import pe.edu.upeu.celmax.service.IUsuarioService;

import java.util.Optional;

public class UsuarioServiceImp extends CrudGenericoServiceImp<Usuario, String> implements IUsuarioService {

    private final UsuarioRepository repo;

    public UsuarioServiceImp(UsuarioRepository repo) {
        this.repo = repo;
    }

    @Override
    protected ICrudGenericoRepository<Usuario, String> getRepo() {
        return repo;
    }

    @Override
    public Optional<Usuario> loginUsuario(String usuario, String clave) {
        return repo.findByUsuarioAndClave(usuario, clave);
    }
}
