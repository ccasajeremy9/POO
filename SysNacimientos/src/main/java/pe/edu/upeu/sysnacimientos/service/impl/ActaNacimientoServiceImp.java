package pe.edu.upeu.sysnacimientos.service.impl;

import pe.edu.upeu.sysnacimientos.model.ActaNacimiento;
import pe.edu.upeu.sysnacimientos.repository.ActaNacimientoRepository;
import pe.edu.upeu.sysnacimientos.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysnacimientos.service.IActaNacimientoService;

public class ActaNacimientoServiceImp extends CrudGenericoServiceImp<ActaNacimiento, Long> implements IActaNacimientoService {

    private final ActaNacimientoRepository actaNacimientoRepository;

    public ActaNacimientoServiceImp(ActaNacimientoRepository actaNacimientoRepository) {
        this.actaNacimientoRepository = actaNacimientoRepository;
    }

    @Override
    protected ICrudGenericoRepository<ActaNacimiento, Long> getRepo() {
        return actaNacimientoRepository;
    }
}
