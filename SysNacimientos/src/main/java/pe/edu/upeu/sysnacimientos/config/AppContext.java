package pe.edu.upeu.sysnacimientos.config;

import pe.edu.upeu.sysnacimientos.controller.*;
import pe.edu.upeu.sysnacimientos.repository.*;
import pe.edu.upeu.sysnacimientos.service.*;
import pe.edu.upeu.sysnacimientos.service.impl.*;
import java.util.HashMap;
import java.util.Map;

public class AppContext {

    private static AppContext instance;

    public static synchronized AppContext getInstance() {
        if (instance == null) instance = new AppContext();
        return instance;
    }

    private final Map<Class<?>, Object> contenedor = new HashMap<>();

    private AppContext() {
        registrarRepositorios();
        registrarServicios();
        registrarControladores();
    }

    private void registrarRepositorios() {
        registrar(ActaNacimientoRepository.class, new ActaNacimientoRepository());
    }

    private void registrarServicios() {
        registrar(IActaNacimientoService.class, new ActaNacimientoServiceImp(getBean(ActaNacimientoRepository.class)));
    }


    private void registrarControladores() {
        registrar(MainguiController.class, new MainguiController());
        registrar(ActaNacimientoController.class,
                new ActaNacimientoController(getBean(IActaNacimientoService.class)));
    }

    private void registrar(Class<?> tipo, Object bean) {
        contenedor.put(tipo, bean);
    }

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