package pl.rotkom.friday.core.repository.common;

import java.util.List;

public interface IGenericDao<T> {

    List<Long> getIds();

    T getById(Long id);

    void save(T entity);

    List<T> getAll();
}