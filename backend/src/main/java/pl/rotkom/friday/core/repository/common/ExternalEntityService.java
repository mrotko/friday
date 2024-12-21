package pl.rotkom.friday.core.repository.common;

import java.util.List;
import java.util.Map;

import jakarta.transaction.Transactional;
import pl.rotkom.friday.core.repository.model.common.ExternalEntity;

public abstract class ExternalEntityService<T extends ExternalEntity> extends GenericService<T> implements IExternalEntityService<T> {

    private final IExternalEntityDao<T> dao;

    public ExternalEntityService(IExternalEntityDao<T> dao) {
        super(dao);
        this.dao = dao;
    }

    @Override
    @Transactional
    public Map<String, Long> getExternalIdToIdMappingAll() {
        return dao.getExternalIdToIdMapping();
    }

    @Override
    @Transactional
    public Map<Long, String> getIdToExternalIdMappingAll() {
        return dao.getIdToExternalIdMapping();
    }

    @Override
    @Transactional
    public List<String> getExternalIds() {
        return dao.getExternalIds();
    }
}
