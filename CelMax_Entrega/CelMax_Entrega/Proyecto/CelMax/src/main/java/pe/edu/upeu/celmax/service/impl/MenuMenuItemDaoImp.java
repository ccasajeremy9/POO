package pe.edu.upeu.celmax.service.impl;

import pe.edu.upeu.celmax.dto.MenuMenuItenTO;
import pe.edu.upeu.celmax.service.IMenuMenuItemDao;

import java.util.*;

/**
 * Arma el menú según el perfil del usuario (igual que en SysVentas).
 * tipoTab: "T" = se abre en una pestaña, "C" = cambia la ventana (Salir → login).
 * Los ítems de un mismo menú deben ir seguidos en la lista.
 */
public class MenuMenuItemDaoImp implements IMenuMenuItemDao {

    @Override
    public List<MenuMenuItenTO> listaAccesos(String perfil, Properties idioma) {
        String mPrincipal = idioma.getProperty("menu.nombre.principal", "Principal");
        String mCatalogo = idioma.getProperty("menu.nombre.catalogo", "Catálogo");
        String mAlmacen = idioma.getProperty("menu.nombre.almacen", "Almacén");
        String mVentas = idioma.getProperty("menu.nombre.ventas", "Ventas");
        String mAdmin = idioma.getProperty("menu.nombre.admin", "Administración");

        // 0
        MenuMenuItenTO inicio = new MenuMenuItenTO("miinicio", "/view/main_inicio.fxml", mPrincipal,
                idioma.getProperty("menuitem.nombre.inicio", "Inicio"), "Inicio", "T");
        MenuMenuItenTO salir = new MenuMenuItenTO("misalir", "/view/login.fxml", mPrincipal,
                idioma.getProperty("menuitem.nombre.salir", "Cerrar sesión"), "Salir", "C");
        MenuMenuItenTO celular = new MenuMenuItenTO("micelular", "/view/main_celular.fxml", mCatalogo,
                idioma.getProperty("menuitem.nombre.celular", "Celulares"), "Gestión de Celulares", "T");
        MenuMenuItenTO accesorio = new MenuMenuItenTO("miaccesorio", "/view/main_accesorio.fxml", mCatalogo,
                idioma.getProperty("menuitem.nombre.accesorio", "Accesorios"), "Gestión de Accesorios", "T");
        MenuMenuItenTO sucursal = new MenuMenuItenTO("misucursal", "/view/main_sucursal.fxml", mAlmacen,
                idioma.getProperty("menuitem.nombre.sucursal", "Sucursales"), "Gestión de Sucursales", "T");
        MenuMenuItenTO inventario = new MenuMenuItenTO("miinventario", "/view/main_inventario.fxml", mAlmacen,
                idioma.getProperty("menuitem.nombre.inventario", "Inventario"), "Inventario de Celulares", "T");
        MenuMenuItenTO cliente = new MenuMenuItenTO("micliente", "/view/main_cliente.fxml", mVentas,
                idioma.getProperty("menuitem.nombre.cliente", "Clientes"), "Gestión de Clientes", "T");
        MenuMenuItenTO venta = new MenuMenuItenTO("miventa", "/view/main_venta.fxml", mVentas,
                idioma.getProperty("menuitem.nombre.venta", "Registrar venta"), "Registrar Venta", "T");
        MenuMenuItenTO historial = new MenuMenuItenTO("mihistorial", "/view/main_historial.fxml", mVentas,
                idioma.getProperty("menuitem.nombre.historial", "Historial de ventas"), "Historial de Ventas", "T");
        MenuMenuItenTO usuario = new MenuMenuItenTO("miusuario", "/view/main_usuario.fxml", mAdmin,
                idioma.getProperty("menuitem.nombre.usuario", "Usuarios"), "Gestión de Usuarios", "T");

        List<MenuMenuItenTO> accesoReal = new ArrayList<>();
        accesoReal.add(inicio);
        accesoReal.add(salir);

        switch (perfil) {
            case "Administrador":
                accesoReal.addAll(List.of(celular, accesorio, sucursal, inventario,
                        cliente, venta, historial, usuario));
                break;
            case "Gerente":
                accesoReal.addAll(List.of(celular, accesorio, sucursal, inventario,
                        cliente, historial));
                break;
            case "Vendedor":
                accesoReal.addAll(List.of(cliente, venta, historial));
                break;
            default:
                break;
        }
        return accesoReal;
    }

    @Override
    public Map<String, String[]> accesosAutorizados(List<MenuMenuItenTO> accesos) {
        Map<String, String[]> menuConfig = new HashMap<>();
        for (MenuMenuItenTO menu : accesos) {
            menuConfig.put("mi" + menu.getIdNombreObj(), new String[]{menu.getRutaFile(), menu.getNombreTab(), menu.getTipoTab()});
        }
        return menuConfig;
    }

    @Override
    public int[] contarMenuMunuItem(List<MenuMenuItenTO> data) {
        int menui = 0, menuitem = 0;
        String menuN = "";
        for (MenuMenuItenTO mmiItem : data) {
            if (!mmiItem.getMenunombre().equals(menuN)) {
                menuN = mmiItem.getMenunombre();
                menui++;
            }
            if (!mmiItem.getMenuitemnombre().equals("")) {
                menuitem++;
            }
        }
        return new int[]{menui, menuitem};
    }
}
