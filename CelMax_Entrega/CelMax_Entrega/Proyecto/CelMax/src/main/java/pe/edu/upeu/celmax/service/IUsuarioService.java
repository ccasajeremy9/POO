package pe.edu.upeu.celmax.service;

import pe.edu.upeu.celmax.model.Usuario;

import java.util.Optional;

public interface IUsuarioService extends ICrudGenericoService<Usuario, String> {
    Optional<Usuario> loginUsuario(String usuario, String clave);
}
