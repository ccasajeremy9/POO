package pe.edu.upeu.sysnacimientos.repository;

import pe.edu.upeu.sysnacimientos.model.ActaNacimiento;

public class ActaNacimientoRepository extends AbstractJpaRepository<ActaNacimiento, Long>{
    private long sequence=1;
    @Override
    protected Long getId(ActaNacimiento entity) {
        return entity.getIdActa();
    }

    @Override
    protected void setId(ActaNacimiento entity, Long id) {
        entity.setIdActa(id);
    }

    @Override
    protected Long generateId() {
        return sequence++;
    }
}
