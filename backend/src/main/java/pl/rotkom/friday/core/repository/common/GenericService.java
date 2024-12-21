package pl.rotkom.friday.core.repository.common;

import java.util.List;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class GenericService<T> implements IGenericService<T> {

    private final IGenericDao<T> dao;

    @Override
    @Transactional
    public T getById(Long id) {
        return dao.getById(id);
    }

    @Override
    @Transactional
    public void save(T entity) {
        dao.save(entity);
    }

    @Override
    @Transactional
    public List<T> getAll() {
        return dao.getAll();
    }

    @Override
    @Transactional
    public List<Long> getIds() {
        return dao.getIds();
    }
}
