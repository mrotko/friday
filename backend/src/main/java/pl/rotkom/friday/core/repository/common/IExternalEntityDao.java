package pl.rotkom.friday.core.repository.common;

import java.util.List;
import java.util.Map;

import pl.rotkom.friday.core.repository.model.common.ExternalEntity;

public interface IExternalEntityDao<T extends ExternalEntity> extends IGenericDao<T> {

    List<String> getExternalIds();

    Map<String, Long> getExternalIdToIdMapping();

    Map<Long, String> getIdToExternalIdMapping();
}
