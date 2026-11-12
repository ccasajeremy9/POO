package pe.edu.upeu.celmax.config;

import pe.edu.upeu.celmax.controller.*;
import pe.edu.upeu.celmax.repository.*;
import pe.edu.upeu.celmax.service.*;
import pe.edu.upeu.celmax.service.impl.*;

import java.util.HashMap;
import java.util.Map;

public class AppContext {

    // Singleton: una sola instancia en toda la app
    private static AppContext instance;

    public static synchronized AppContext getInstance() {
        if (instance == null) instance = new AppContext();
        return instance;
    }

    // El "directorio": Clase → Objeto
    private final Map<Class<?>, Object> contenedor = new HashMap<>();

    // Constructor privado: aquí se arma toda la aplicación
    private AppContext() {
        registrarRepositorios();
        registrarServicios();
        registrarControladores();
    }

    // CAPA 1 — REPOSITORIOS
    // Cada repositorio sabe hablar con una tabla de la base de datos.
    private void registrarRepositorios() {
        registrar(CategoriaRepository.class,  new CategoriaRepository());
        registrar(PerfilRepository.class,     new PerfilRepository());
        registrar(UsuarioRepository.class,    new UsuarioRepository());
        registrar(ClienteRepository.class,    new ClienteRepository());
        registrar(CelularRepository.class,    new CelularRepository());
        registrar(AccesorioRepository.class,  new AccesorioRepository());
        registrar(SucursalRepository.class,   new SucursalRepository());
        registrar(InventarioRepository.class, new InventarioRepository());
        registrar(VentaRepository.class,      new VentaRepository());
    }

    // CAPA 2 — SERVICIOS
    // Cada servicio recibe su repositorio por constructor.
    private void registrarServicios() {
        registrar(ICategoriaService.class,  new CategoriaServiceImp(getBean(CategoriaRepository.class)));
        registrar(IPerfilService.class,     new PerfilServiceImp(getBean(PerfilRepository.class)));
        registrar(IUsuarioService.class,    new UsuarioServiceImp(getBean(UsuarioRepository.class)));
        registrar(IClienteService.class,    new ClienteServiceImp(getBean(ClienteRepository.class)));
        registrar(ICelularService.class,    new CelularServiceImp(getBean(CelularRepository.class)));
        registrar(IAccesorioService.class,  new AccesorioServiceImp(getBean(AccesorioRepository.class)));
        registrar(ISucursalService.class,   new SucursalServiceImp(getBean(SucursalRepository.class)));
        registrar(IInventarioService.class, new InventarioServiceImp(getBean(InventarioRepository.class)));
        registrar(IVentaService.class,      new VentaServiceImp(
                getBean(VentaRepository.class),
                getBean(InventarioRepository.class),
                getBean(AccesorioRepository.class)));

        registrar(IMenuMenuItemDao.class, new MenuMenuItemDaoImp());
    }

    // CAPA 3 — CONTROLADORES JavaFX
    // Cada controlador recibe los servicios que necesita por constructor.
    // El FXMLLoader los busca aquí a través de setControllerFactory().
    private void registrarControladores() {
        registrar(LoginController.class,   new LoginController(getBean(IUsuarioService.class)));
        registrar(MainguiController.class, new MainguiController(getBean(IMenuMenuItemDao.class)));
        registrar(InicioController.class,  new InicioController(
                getBean(IVentaService.class),
                getBean(IClienteService.class),
                getBean(IInventarioService.class),
                getBean(IAccesorioService.class)));

        registrar(ClienteController.class,   new ClienteController(getBean(IClienteService.class)));
        registrar(CelularController.class,   new CelularController(
                getBean(ICelularService.class), getBean(ICategoriaService.class)));
        registrar(AccesorioController.class, new AccesorioController(
                getBean(IAccesorioService.class), getBean(ICategoriaService.class)));
        registrar(SucursalController.class,  new SucursalController(getBean(ISucursalService.class)));
        registrar(InventarioController.class, new InventarioController(
                getBean(IInventarioService.class),
                getBean(ICelularService.class),
                getBean(ISucursalService.class)));
        registrar(UsuarioController.class,   new UsuarioController(
                getBean(IUsuarioService.class), getBean(IPerfilService.class)));

        registrar(VentaController.class, new VentaController(
                getBean(IVentaService.class), getBean(IClienteService.class)));
        registrar(HistorialVentaController.class, new HistorialVentaController(getBean(IVentaService.class)));
    }

    // API del contenedor — estos dos métodos son todo lo que hace la DI

    /** Guarda un objeto en el directorio, indexado por su tipo o interfaz. */
    private void registrar(Class<?> tipo, Object bean) {
        contenedor.put(tipo, bean);
    }

    /**
     * Busca y devuelve un objeto por su tipo o interfaz.
     * Equivale a lo que hace Spring/Micronaut con @Inject automáticamente.
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> tipo) {
        Object bean = contenedor.get(tipo);
        if (bean == null) {
            bean = contenedor.values().stream()
                    .filter(b -> tipo.isAssignableFrom(b.getClass()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(
                            "Bean no encontrado: " + tipo.getName() +
                                    "\n→ ¿Lo registraste en AppContext?"));
        }
        return (T) bean;
    }
}
