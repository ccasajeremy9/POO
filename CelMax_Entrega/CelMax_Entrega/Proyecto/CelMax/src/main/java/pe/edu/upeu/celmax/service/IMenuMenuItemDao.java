package pe.edu.upeu.celmax.service;

import pe.edu.upeu.celmax.dto.MenuMenuItenTO;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface IMenuMenuItemDao {
    List<MenuMenuItenTO> listaAccesos(String perfil, Properties idioma);
    Map<String, String[]> accesosAutorizados(List<MenuMenuItenTO> accesos);
    int[] contarMenuMunuItem(List<MenuMenuItenTO> data);
}
