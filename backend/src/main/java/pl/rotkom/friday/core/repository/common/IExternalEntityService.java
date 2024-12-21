package pl.rotkom.friday.core.repository.common;

import java.util.List;
import java.util.Map;

import pl.rotkom.friday.core.repository.model.common.ExternalEntity;

public interface IExternalEntityService<T extends ExternalEntity> extends IGenericService<T>{

    List<String> getExternalIds();

    Map<String, Long> getExternalIdToIdMappingAll();

    Map<Long, String> getIdToExternalIdMappingAll();
}
